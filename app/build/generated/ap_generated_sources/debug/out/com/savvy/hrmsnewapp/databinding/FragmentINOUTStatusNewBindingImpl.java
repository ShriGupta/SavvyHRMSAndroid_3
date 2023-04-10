package com.savvy.hrmsnewapp.databinding;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentINOUTStatusNewBindingImpl extends FragmentINOUTStatusNewBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.date_layout, 1);
        sViewsWithIds.put(R.id.txtInOut_Date, 2);
        sViewsWithIds.put(R.id.btn_InOutdate, 3);
        sViewsWithIds.put(R.id.card_view, 4);
        sViewsWithIds.put(R.id.supplier_name_tv, 5);
        sViewsWithIds.put(R.id.activity_name_tv, 6);
        sViewsWithIds.put(R.id.location_tv, 7);
        sViewsWithIds.put(R.id.location_type_tv, 8);
        sViewsWithIds.put(R.id.meeting_type_tv, 9);
        sViewsWithIds.put(R.id.work_type_tv, 10);
        sViewsWithIds.put(R.id.charges_type_tv, 11);
        sViewsWithIds.put(R.id.toll_tv, 12);
        sViewsWithIds.put(R.id.check_in_time_tv, 13);
        sViewsWithIds.put(R.id.data_not_found_tv, 14);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentINOUTStatusNewBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 15, sIncludes, sViewsWithIds));
    }
    private FragmentINOUTStatusNewBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[6]
            , (android.widget.Button) bindings[3]
            , (androidx.cardview.widget.CardView) bindings[4]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[14]
            , (android.widget.LinearLayout) bindings[1]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[12]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[2]
            , (android.widget.TextView) bindings[10]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
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