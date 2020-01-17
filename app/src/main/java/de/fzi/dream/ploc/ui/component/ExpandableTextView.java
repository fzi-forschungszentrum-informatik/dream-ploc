package de.fzi.dream.ploc.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.utility.Constants;

/**
 * The ExpandableTextView is an extension for the TextView to make an TextView expandable on click.
 */
public class ExpandableTextView extends AppCompatTextView {
    private CharSequence expandedText;
    private CharSequence collapsedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    /**
     * Constructor
     *
     * @param context The Context the view is embedded in.
     */
    public ExpandableTextView(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context The Context the view is embedded in.
     * @param attrs   Style attributes
     */
    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, Constants.COLLAPSED_TEXT_LENGTH);
        typedArray.recycle();
        setOnClickListener(v -> {
            trim = !trim;
            setText();
        });
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? collapsedText : expandedText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        expandedText = text;
        collapsedText = getCollapsedText();
        bufferType = type;
        setText();
    }

    private CharSequence getCollapsedText() {
        if (expandedText != null && expandedText.length() > trimLength) {
            return new SpannableStringBuilder(expandedText, 0, trimLength + 1).append(Constants.COLLAPSED_ELLIPSIS);
        } else {
            return expandedText;
        }
    }
}