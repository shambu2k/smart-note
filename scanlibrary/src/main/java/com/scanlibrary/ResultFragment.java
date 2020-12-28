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

import java.io.IOException;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    // TODO: Refine detection after getting finalized page.
    //  Image rotating after taking picture - fix that.
    //  Crashing with weird error - Textview cast to radiogroup??

    private View view;
    private ImageView scannedImageView;
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
    private Bitmap magicColorBitmap;
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
                    magicColorBitmap = ((ScanActivity) getActivity()).getMagicColorBitmap(original);
                    int r = ((ScanActivity) getActivity()).getSubjectUnit(magicColorBitmap);
                    setRadioButtons(r);
                    magicColorBitmap.recycle();
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
        if(r/10 != 0 && r%10 != 0) {
            subjectRadioGroup.check(((RadioButton)subjectRadioGroup.getChildAt(r/10)).getId());
            unitRadioGroup.check(((RadioButton)unitRadioGroup.getChildAt(r%10)).getId());
        } else if(r/10 != 0 && r%10 == 0) subjectRadioGroup.check(((RadioButton)subjectRadioGroup.getChildAt(r/10)).getId());
        else if(r/10 == 0 && r%10 != 0) unitRadioGroup.check(((RadioButton)unitRadioGroup.getChildAt(r%10)).getId());
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
}