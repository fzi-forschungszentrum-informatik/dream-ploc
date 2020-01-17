/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fzi.dream.ploc.ui.fragment;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.RecordDetail;
import de.fzi.dream.ploc.ui.activity.FeedbackActivity;
import de.fzi.dream.ploc.ui.dialog.NotificationDialog;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.viewmodel.RecordDetailViewModel;

import static android.content.Context.DOWNLOAD_SERVICE;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;

/**
 * The FeedbackPDFViewFragment is embedded in the {@link FeedbackActivity} and manages displaying of
 * an PDF per page in an ImageView. This Fragment is based on this implementation
 * https://github.com/googlearchive/android-PdfRendererBasic from Google.
 */
public class FeedbackPDFViewFragment extends Fragment implements View.OnClickListener {

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";
    /**
     * The context of the fragment.
     */
    private Context mContext;
    /**
     * The id of the record.
     */
    private int mRecordID;
    /**
     * The download link of the PDF.
     */
    private String mPDFurl;
    /**
     * The file name the cached PDF is saved.
     */
    private String mFileName;
    /**
     * PDF loading progress bar.
     */
    private ProgressDialog mProgressDialog;
    /**
     * The object containing all details of the shown record.
     */
    private RecordDetail mRecordDetail;
    /**
     * ViewModel for data requests
     */
    private RecordDetailViewModel mViewModel;
    /**
     * ID of the current downloading process.
     */
    private Long downloadID;
    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;
    /**
     * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;
    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;
    /**
     * {@link android.widget.ImageView} that shows a PDF page as a {@link android.graphics.Bitmap}
     */
    private ImageView mImageView;
    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button mButtonPrevious;
    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button mButtonNext;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    /**
     * PDF page index
     */
    private int mPageIndex;
    private Button mButtonGoToRating;
    private OnInteractionListener mListener;
    /**
     * BroadcastReceiver triggered when the download is completed.
     */
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                final File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mFileName);
                try {
                    openRenderer(tempFile);
                    showPage(mPageIndex);
                } catch (IOException e) {
                    mProgressDialog.dismiss();
                    NotificationDialog dialog = new NotificationDialog(getResources()
                            .getString(R.string.dialog_title_pdf_error), getResources().getString(R.string.dialog_text_pdf_error));
                    if (getActivity() != null) {
                        dialog.show(getActivity().getSupportFragmentManager(), NotificationDialog.TAG);
                    }
                }
            }
        }
    };

    /**
     * Fragments require an empty public constructor.
     */
    public FeedbackPDFViewFragment() {
    }

    /**
     * Create a new instance of the RecordDetailFragment.
     *
     * @return RecordDetailFragment
     */
    public static FeedbackPDFViewFragment newInstance(int publicationID) {
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_ID, publicationID);
        FeedbackPDFViewFragment fragment = new FeedbackPDFViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /*
    Lifecycle Methods
    */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_pdf_view, container, false);
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mScaleGestureDetector.onTouchEvent(event);
                v.performClick();
            }
            return true;
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        mImageView = view.findViewById(R.id.image_view_pdf_view_fragment);
        mButtonPrevious = view.findViewById(R.id.button_text_previous_page_pdf_view_fragment);
        mButtonNext = view.findViewById(R.id.button_next_page_pdf_view_fragment);
        mButtonGoToRating = view.findViewById(R.id.button_give_feedback_pdf_view_fragment);

        // Bind events.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mPageIndex = 0;

        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        // Register listener and receiver
        mListener = (OnInteractionListener) getActivity();
        mButtonGoToRating.setOnClickListener(view1 -> mListener.onOpenFeedbackRating());
        if (getArguments() != null) {
            mRecordID = getArguments().getInt(EXTRA_ID);
            mViewModel = obtainViewModel();
            observeDetails();
        }
        mContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        try {
            closeRenderer();
            mContext.unregisterReceiver(onDownloadComplete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer(File pdf) throws IOException {
        mFileDescriptor = ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    /**
     * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        if(mPdfRenderer != null){
            mPdfRenderer.close();
        }
        if(mFileDescriptor != null){
           mFileDescriptor.close();
        }
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        mImageView.setImageBitmap(bitmap);
        mProgressDialog.dismiss();
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        getActivity().setTitle(getString(R.string.title_activity_pdf, index + 1, pageCount));
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        if (!mButtonNext.isEnabled()) {
            mButtonGoToRating.setVisibility(View.VISIBLE);
            // Zoom animation.
            Animation animFadeIn = AnimationUtils.loadAnimation(getContext(),
                    R.anim.fade_in_button);
            mButtonGoToRating.startAnimation(animFadeIn);
        }


    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    /**
     * Download a PDF to the download folder of the android system. If there is no PDF link available
     * show a snackbar message.
     */
    private void downloadPDF(String url) {
        mProgressDialog = ProgressDialog.show(mContext, getResources().getString(R.string.progress_dialog_pdf_title), getResources().getString(R.string.progress_dialog_pdf_text), true);
        String filename = mRecordDetail.getTitle().replaceAll(" ", "_").toLowerCase().replaceAll("\\W+", "");
        mFileName = filename + ".pdf";
        if (url != null && !url.equals("") && URLUtil.isValidUrl(url)) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle(getResources().getString(R.string.notification_download_message))
                    .setDescription(mRecordDetail.getTitle())
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);
        }
    }

    /**
     * Check and request permission to write to the file system. If permission is already granted,
     * download the PDF.
     */
    private void checkPermissionOrDownload(String url) {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                    101);
        } else {
            downloadPDF(url);
        }
    }

    /**
     * This method is called when the user accepts or decline the permission requested
     * from #checkPermissionOrDownload.
     *
     * @param requestCode  The requestCode is used to check which permission called this function.
     * @param permissions  This array contains the android uri for the permissions.
     * @param grantResults This array contains the result of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPDF(mPDFurl);
            } else {
                NotificationDialog dialog = new NotificationDialog(getResources()
                        .getString(R.string.dialog_title_no_pdf), getResources().getString(R.string.dialog_text_no_pdf));
                if (getActivity() != null) {
                    dialog.show(getActivity().getSupportFragmentManager(), NotificationDialog.TAG);
                }
            }
        }
    }

    /*
        ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return RecordDetailViewModel
     */
    private RecordDetailViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(RecordDetailViewModel.class);
    }

    /**
     * Observe and react to changes on record detail updates.
     */
    private void observeDetails() {
        mViewModel.getDetails(mRecordID).observe(getViewLifecycleOwner(), recordDetail -> {
            if (recordDetail != null) {
                mRecordDetail = recordDetail;
                if (recordDetail.getPDFLink() != null && !recordDetail.getPDFLink().equals("")) {
                    mPDFurl = mRecordDetail.getPDFLink();
                    checkPermissionOrDownload(mPDFurl);
                } else {
                    NotificationDialog dialog = new NotificationDialog(getResources()
                            .getString(R.string.dialog_title_no_pdf), getResources().getString(R.string.dialog_text_no_pdf));
                    if (getActivity() != null) {
                        dialog.show(getActivity().getSupportFragmentManager(), NotificationDialog.TAG);
                    }
                    mPDFurl = Constants.PLACEHOLDER_PDF;
                    checkPermissionOrDownload(mPDFurl);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_text_previous_page_pdf_view_fragment: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.button_next_page_pdf_view_fragment: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

    public interface OnInteractionListener {
        void onOpenFeedbackRating();

        void onOpenFeedbackPDF();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }

}
