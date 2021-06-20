package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
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
import java.util.List;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    // TODO: Refine detection after getting finalized page.

    private View view;
    private ImageView scannedImageView;
    private ImageView processedImgV;
    private Bitmap original;
    private Button originalButton;
    private Button MagicColorButton;
    private Button grayModeButton;
    private Button bwButton;
    private Button exitButton;
    private Button rotateLeftButton;
    private Button rotateRightButton;
    private Button doneButton;
    private RadioGroup subjectRadioGroup;
    private RadioButton subRadioButton;
    private RadioGroup unitRadioGroup;
    private RadioButton unitRadioButton;
    private Bitmap transformed;
    private Bitmap processed;
    private static ProgressDialogFragment progressDialogFragment;

    public ResultFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        init();
        return view;
    }

    private void init() {
        scannedImageView = (ImageView) view.findViewById(R.id.scannedImage);
        processedImgV = (ImageView) view.findViewById(R.id.processedImage);
        originalButton = (Button) view.findViewById(R.id.original);
        originalButton.setOnClickListener(new OriginalButtonClickListener());
        MagicColorButton = (Button) view.findViewById(R.id.magicColor);
        MagicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        grayModeButton = (Button) view.findViewById(R.id.grayMode);
        grayModeButton.setOnClickListener(new GrayButtonClickListener());
        bwButton = (Button) view.findViewById(R.id.BWMode);
        bwButton.setOnClickListener(new BWButtonClickListener());
        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);
        exitButton = (Button) view.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new ExitButtonClickListener());
        rotateLeftButton = (Button) view.findViewById(R.id.rotate_left);
        rotateLeftButton.setOnClickListener(new RotateLeftButtonClickListener());
        rotateRightButton = (Button) view.findViewById(R.id.rotate_right);
        rotateRightButton.setOnClickListener(new RotateRightButtonClickListener());
        subjectRadioGroup = view.findViewById(R.id.sub_grp);
        unitRadioGroup = view.findViewById(R.id.unit_grp);
        doneButton = (Button) view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new DoneButtonClickListener());
        initRadioButtons();
    }

    private int initText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        final int[] ans = {0};
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                Log.i("ocr","success");
                                String resultText = visionText.getText();
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    int lineNo=0;
                                    boolean flag=false;
                                    for (Text.Line line : block.getLines()) {

                                        for (Text.Element element : line.getElements()) {
                                            String elementText = element.getText();
                                            char[] chars = elementText.toCharArray();

                                            for(char c : chars){
                                                if(Character.isDigit(c)){
                                                    if(lineNo==0) {
                                                        ans[0] += (10 * (c - '0'));
                                                        Log.i("ocrRes",Integer.toString(ans[0]));
                                                        lineNo=1;
                                                    }
                                                    else if(lineNo==1){
                                                        ans[0]+=(c-'0');
                                                        setRadioButtons(ans[0]);
                                                        Log.i("ocrRes",Integer.toString(ans[0]));
                                                        flag=true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(flag)
                                                break;
                                            Log.i("ocr",elementText);
                                        }
                                        if(flag)
                                            break;
                                    }
                                    if(flag)
                                        break;
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.i("ocr","failed");
                                    }
                                });
        return ans[0];

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

        processed = ((ScanActivity) getActivity()).getProcessedBitmap(original);
        processedImgV.setImageBitmap(processed);
    }

    private void initRadioButtons() {
        showProgressDialog(getResources().getString(R.string.detecting_sub_unit));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //int r = ((ScanActivity) getActivity()).getSubjectUnit(original);
                    int r = initText(original);
                    Log.i("ocrRadio",Integer.toString(r));
                   // setRadioButtons(r);
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
        if(r<=55) {
            if(r/10 != 0 && r%10 != 0) {
                subjectRadioGroup.check(((RadioButton)subjectRadioGroup.getChildAt(r/10)).getId());
                unitRadioGroup.check(((RadioButton)unitRadioGroup.getChildAt(r%10)).getId());
            } else if(r/10 != 0 && r%10 == 0) subjectRadioGroup.check(((RadioButton)subjectRadioGroup.getChildAt(r/10)).getId());
            else if(r/10 == 0 && r%10 != 0) unitRadioGroup.check(((RadioButton)unitRadioGroup.getChildAt(r%10)).getId());
        }
    }

    private class ExitButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

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
            if(subjectRadioGroup.getCheckedRadioButtonId() == -1 && unitRadioGroup.getCheckedRadioButtonId() == -1) {
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
                            subRadioButton = (RadioButton) view.findViewById(subjectRadioGroup.getCheckedRadioButtonId());
                            unitRadioButton = (RadioButton) view.findViewById(unitRadioGroup.getCheckedRadioButtonId());
                            Uri uri = Utils.getUri(getActivity(), bitmap);
                            data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                            data.putExtra(ScanConstants.SCANNED_SUB, subjectRadioGroup.indexOfChild(subRadioButton));
                            data.putExtra(ScanConstants.SCANNED_UNIT, unitRadioGroup.indexOfChild(unitRadioButton)  );
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
            matrix.postRotate(degrees, bitMapOrg.getWidth()/2, bitMapOrg.getHeight()/2);
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