package com.savvy.hrmsnewapp.databinding;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class InOutRequestFragmentBindingImpl extends InOutRequestFragmentBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.coordinatorLayout, 1);
        sViewsWithIds.put(R.id.txtInOut_Date, 2);
        sViewsWithIds.put(R.id.btn_InOutdate, 3);
        sViewsWithIds.put(R.id.customTextView, 4);
        sViewsWithIds.put(R.id.btn_spin_select_supplier, 5);
        sViewsWithIds.put(R.id.txtInOut_activityTitle, 6);
        sViewsWithIds.put(R.id.edt_InOut_activity, 7);
        sViewsWithIds.put(R.id.txtInOut_locationTitle, 8);
        sViewsWithIds.put(R.id.edt_InOut_location, 9);
        sViewsWithIds.put(R.id.txtInOut_subLocationTitle, 10);
        sViewsWithIds.put(R.id.edt_InOut_sublocation, 11);
        sViewsWithIds.put(R.id.radioGroup, 12);
        sViewsWithIds.put(R.id.ncr_radio, 13);
        sViewsWithIds.put(R.id.non_ncr_radio, 14);
        sViewsWithIds.put(R.id.edt_InOut_conveyance, 15);
        sViewsWithIds.put(R.id.meeting_type_layout, 16);
        sViewsWithIds.put(R.id.meetingTextView, 17);
        sViewsWithIds.put(R.id.spin_meeting_type, 18);
        sViewsWithIds.put(R.id.btn_spin_meeting_type, 19);
        sViewsWithIds.put(R.id.workTextView, 20);
        sViewsWithIds.put(R.id.spin_comp_status, 21);
        sViewsWithIds.put(R.id.btn_spin_work_type, 22);
        sViewsWithIds.put(R.id.chargesTextView, 23);
        sViewsWithIds.put(R.id.spin_charge_type, 24);
        sViewsWithIds.put(R.id.btn_spin_charges_type, 25);
        sViewsWithIds.put(R.id.btn_od_to_time, 26);
        sViewsWithIds.put(R.id.ncr_view_layout, 27);
        sViewsWithIds.put(R.id.edt_InOut_amount, 28);
        sViewsWithIds.put(R.id.txt_Document, 29);
        sViewsWithIds.put(R.id.lr_MedicalCertificateLayout, 30);
        sViewsWithIds.put(R.id.btn_TEuploadFile, 31);
        sViewsWithIds.put(R.id.add_multiple_file, 32);
        sViewsWithIds.put(R.id.txt_AddMore, 33);
        sViewsWithIds.put(R.id.non_ncr_view_layout, 34);
        sViewsWithIds.put(R.id.hotel_book_spinner, 35);
        sViewsWithIds.put(R.id.hotel_amount_etv, 36);
        sViewsWithIds.put(R.id.btn_hotel_uploadFile, 37);
        sViewsWithIds.put(R.id.flight_book_spinner, 38);
        sViewsWithIds.put(R.id.flight_amount_etv, 39);
        sViewsWithIds.put(R.id.btn_flight_uploadFile, 40);
        sViewsWithIds.put(R.id.train_book_spinner, 41);
        sViewsWithIds.put(R.id.train_amount_etv, 42);
        sViewsWithIds.put(R.id.btn_train_uploadFile, 43);
        sViewsWithIds.put(R.id.cab_type_layout, 44);
        sViewsWithIds.put(R.id.CabTextView, 45);
        sViewsWithIds.put(R.id.spin_cab_type, 46);
        sViewsWithIds.put(R.id.spin_dropdown_type, 47);
        sViewsWithIds.put(R.id.cab_amount, 48);
        sViewsWithIds.put(R.id.btn_cabuploadFile, 49);
        sViewsWithIds.put(R.id.other_layout, 50);
        sViewsWithIds.put(R.id.edt_InOut_other, 51);
        sViewsWithIds.put(R.id.btn_otheruploadFile, 52);
        sViewsWithIds.put(R.id.edt_InOut_remarks, 53);
        sViewsWithIds.put(R.id.btn_od_request_submit, 54);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public InOutRequestFragmentBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 55, sIncludes, sViewsWithIds));
    }
    private InOutRequestFragmentBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[45]
            , (androidx.recyclerview.widget.RecyclerView) bindings[32]
            , (android.widget.Button) bindings[49]
            , (android.widget.Button) bindings[40]
            , (android.widget.Button) bindings[37]
            , (android.widget.Button) bindings[3]
            , (android.widget.Button) bindings[54]
            , (android.widget.TextView) bindings[26]
            , (android.widget.Button) bindings[52]
            , (android.widget.Button) bindings[25]
            , (android.widget.Button) bindings[19]
            , (android.widget.Spinner) bindings[5]
            , (android.widget.Button) bindings[22]
            , (android.widget.Button) bindings[31]
            , (android.widget.Button) bindings[43]
            , (android.widget.EditText) bindings[48]
            , (android.widget.LinearLayout) bindings[44]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[23]
            , (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[1]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[4]
            , (android.widget.EditText) bindings[7]
            , (android.widget.EditText) bindings[28]
            , (android.widget.TextView) bindings[15]
            , (android.widget.EditText) bindings[9]
            , (android.widget.EditText) bindings[51]
            , (android.widget.EditText) bindings[53]
            , (android.widget.EditText) bindings[11]
            , (android.widget.EditText) bindings[39]
            , (android.widget.Spinner) bindings[38]
            , (android.widget.EditText) bindings[36]
            , (android.widget.Spinner) bindings[35]
            , (android.widget.LinearLayout) bindings[30]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[17]
            , (android.widget.LinearLayout) bindings[16]
            , (android.widget.RadioButton) bindings[13]
            , (android.widget.LinearLayout) bindings[27]
            , (android.widget.RadioButton) bindings[14]
            , (android.widget.LinearLayout) bindings[34]
            , (android.widget.LinearLayout) bindings[50]
            , (android.widget.RadioGroup) bindings[12]
            , (android.widget.Spinner) bindings[46]
            , (android.widget.Spinner) bindings[24]
            , (android.widget.Spinner) bindings[21]
            , (android.widget.Spinner) bindings[47]
            , (android.widget.Spinner) bindings[18]
            , (android.widget.EditText) bindings[42]
            , (android.widget.Spinner) bindings[41]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[33]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[29]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[6]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[2]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[8]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[10]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[20]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
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