package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    private View view;
    private ImageView scannedImageView;
    private Bitmap original;
    private RadioGroup subjectRadioGroup;
    private RadioButton subRadioButton;
    private RadioGroup unitRadioGroup;
    private RadioButton unitRadioButton;
    private Bitmap transformed;
    private static ProgressDialogFragment progressDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        init();
        return view;
    }

    private void init() {
        scannedImageView = view.findViewById(R.id.scannedImage);
        Button originalButton = view.findViewById(R.id.original);
        originalButton.setOnClickListener(new OriginalButtonClickListener());
        Button magicColorButton = view.findViewById(R.id.magicColor);
        magicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        Button grayModeButton = view.findViewById(R.id.grayMode);
        grayModeButton.setOnClickListener(new GrayButtonClickListener());
        Button bwButton = view.findViewById(R.id.BWMode);
        bwButton.setOnClickListener(new BWButtonClickListener());
        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);
        Button exitButton = view.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new ExitButtonClickListener());
        Button rotateLeftButton = view.findViewById(R.id.rotate_left);
        rotateLeftButton.setOnClickListener(new RotateLeftButtonClickListener());
        Button rotateRightButton = view.findViewById(R.id.rotate_right);
        rotateRightButton.setOnClickListener(new RotateRightButtonClickListener());
        subjectRadioGroup = view.findViewById(R.id.sub_grp);
        unitRadioGroup = view.findViewById(R.id.unit_grp);
        Button doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new DoneButtonClickListener());
        initRadioButtons();
    }

    private void selectRadioButtons(Bitmap bitmap, final int selectedSubject, final int selectedUnit) {
        if (selectedSubject != 0 && selectedUnit != 0) {
            setRadioButtons(selectedSubject * 10 + selectedUnit);
            return;
        }
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                Log.i("ocr", "success");
                                List<Integer> list = new ArrayList<>();
                                outer:
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    for (Text.Line line : block.getLines()) {
                                        for (Text.Element element : line.getElements()) {
                                            for (char c : element.getText().toCharArray()) {
                                                Log.i("ResultFragment", "Detected char: " + c);
                                                if (Character.isDigit(c)) {
                                                    list.add(c - '0');
                                                    if (list.size() == 2) {
                                                        break outer;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (list.size() == 2) {
                                    if (selectedSubject != 0) {
                                        setRadioButtons(selectedSubject * 10 + list.get(1));
                                    } else {
                                        setRadioButtons(list.get(0) + list.get(1));
                                    }
                                } else if (list.size() == 1) {
                                    if (selectedSubject != 0) {
                                        if (selectedSubject == list.get(0)) {
                                            setRadioButtons(selectedSubject * 10);
                                        } else {
                                            setRadioButtons(selectedSubject * 10 + list.get(0));
                                        }
                                    } else {
                                        setRadioButtons(list.get(0) * 10);
                                    }
                                } else {
                                    if (selectedSubject != 0) {
                                        setRadioButtons(selectedSubject * 10);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.i("ocr", "failed");
                                    }
                                });
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            original = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return original;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        scannedImageView.setImageBitmap(scannedImage);
    }

    private void initRadioButtons() {
        showProgressDialog(getResources().getString(R.string.detecting_sub_unit));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    selectRadioButtons(
                            original,
                            getArguments().getInt(ScanConstants.SELECTED_SUBJECT, 0),
                            getArguments().getInt(ScanConstants.SELECTED_UNIT, 0)
                    );
                    dismissDialog();
                } catch (final OutOfMemoryError e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            dismissDialog();
                        }
                    });
                }
            }
        });

    }

    private void setRadioButtons(int r) {
        Log.d("ResultFragment", "setRadioButtons int passed: " + r);
        try {
            if (r <= 55) {
                if (r / 10 != 0 && r % 10 != 0) {
                    subjectRadioGroup.check(subjectRadioGroup.getChildAt(r / 10).getId());
                    unitRadioGroup.check(unitRadioGroup.getChildAt(r % 10).getId());
                } else if (r / 10 != 0 && r % 10 == 0)
                    subjectRadioGroup.check(subjectRadioGroup.getChildAt(r / 10).getId());
                else if (r / 10 == 0 && r % 10 != 0)
                    unitRadioGroup.check(unitRadioGroup.getChildAt(r % 10).getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ExitButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getActivity().getFragmentManager().popBackStack();
        }
    }

    private class RotateLeftButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    transformed = rotateBitmap(-90, transformed != null ? transformed : original);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class RotateRightButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    transformed = rotateBitmap(90, transformed != null ? transformed : original);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class DoneButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (subjectRadioGroup.getCheckedRadioButtonId() == -1 && unitRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getActivity(), "Check the radioButtons", Toast.LENGTH_SHORT).show();
            } else {
                showProgressDialog(getResources().getString(R.string.loading));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent data = new Intent();
                            Bitmap bitmap = transformed;
                            if (bitmap == null) {
                                bitmap = original;
                            }
                            subRadioButton = view.findViewById(subjectRadioGroup.getCheckedRadioButtonId());
                            unitRadioButton = view.findViewById(unitRadioGroup.getCheckedRadioButtonId());
                            Uri uri = Utils.getUri(getActivity(), bitmap);
                            data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                            data.putExtra(ScanConstants.SCANNED_SUB, subjectRadioGroup.indexOfChild(subRadioButton));
                            data.putExtra(ScanConstants.SCANNED_UNIT, unitRadioGroup.indexOfChild(unitRadioButton));
                            getActivity().setResult(Activity.RESULT_OK, data);
                            original.recycle();
                            System.gc();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    getActivity().finish();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private class BWButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ((ScanActivity) getActivity()).getBWBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class MagicColorButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ((ScanActivity) getActivity()).getMagicColorBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class OriginalButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                showProgressDialog(getResources().getString(R.string.applying_filter));
                transformed = original;
                scannedImageView.setImageBitmap(original);
                dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                dismissDialog();
            }
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ((ScanActivity) getActivity()).getGrayBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    private Bitmap rotateBitmap(final int degrees, Bitmap bitMapOrg) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees, bitMapOrg.getWidth() / 2, bitMapOrg.getHeight() / 2);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitMapOrg, bitMapOrg.getWidth(), bitMapOrg.getHeight(), true);
            return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}