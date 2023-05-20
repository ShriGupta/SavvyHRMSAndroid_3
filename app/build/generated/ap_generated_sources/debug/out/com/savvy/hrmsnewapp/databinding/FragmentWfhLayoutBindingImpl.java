package com.savvy.hrmsnewapp.databinding;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentWfhLayoutBindingImpl extends FragmentWfhLayoutBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.txtOD_FromDateTitle, 1);
        sViewsWithIds.put(R.id.btn_from_wfhdate, 2);
        sViewsWithIds.put(R.id.txtOD_ToDateTitle, 3);
        sViewsWithIds.put(R.id.btn_to_wfhdate, 4);
        sViewsWithIds.put(R.id.linear_compareDate, 5);
        sViewsWithIds.put(R.id.txt_compareDate, 6);
        sViewsWithIds.put(R.id.radio_button_fullday, 7);
        sViewsWithIds.put(R.id.radio_button_halfday, 8);
        sViewsWithIds.put(R.id.radio_button_firsthalf, 9);
        sViewsWithIds.put(R.id.radio_button_secondHalf, 10);
        sViewsWithIds.put(R.id.btn_whf_time_from, 11);
        sViewsWithIds.put(R.id.btn_whf_to_time, 12);
        sViewsWithIds.put(R.id.txtOD_ReasonTitle, 13);
        sViewsWithIds.put(R.id.edt_whf_reason, 14);
        sViewsWithIds.put(R.id.btn_whf_request_submit, 15);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentWfhLayoutBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private FragmentWfhLayoutBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[2]
            , (android.widget.Button) bindings[4]
            , (com.google.android.material.button.MaterialButton) bindings[15]
            , (android.widget.Button) bindings[11]
            , (android.widget.Button) bindings[12]
            , (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[0]
            , (android.widget.EditText) bindings[14]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.RadioButton) bindings[9]
            , (android.widget.RadioButton) bindings[7]
            , (android.widget.RadioButton) bindings[8]
            , (android.widget.RadioButton) bindings[10]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[6]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[1]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[13]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[3]
            );
        this.coordinator.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}