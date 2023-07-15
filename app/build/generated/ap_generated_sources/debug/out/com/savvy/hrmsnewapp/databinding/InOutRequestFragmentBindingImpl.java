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
        sViewsWithIds.put(R.id.ll_visit_layout, 4);
        sViewsWithIds.put(R.id.customTextView, 5);
        sViewsWithIds.put(R.id.spinner_visit, 6);
        sViewsWithIds.put(R.id.ll_main_layout, 7);
        sViewsWithIds.put(R.id.btn_spin_select_supplier, 8);
        sViewsWithIds.put(R.id.spinner_activity, 9);
        sViewsWithIds.put(R.id.txtInOut_activityTitle, 10);
        sViewsWithIds.put(R.id.edt_InOut_activity, 11);
        sViewsWithIds.put(R.id.txtInOut_locationTitle, 12);
        sViewsWithIds.put(R.id.edt_InOut_location, 13);
        sViewsWithIds.put(R.id.txtInOut_subLocationTitle, 14);
        sViewsWithIds.put(R.id.edt_InOut_sublocation, 15);
        sViewsWithIds.put(R.id.radioGroup, 16);
        sViewsWithIds.put(R.id.ncr_radio, 17);
        sViewsWithIds.put(R.id.non_ncr_radio, 18);
        sViewsWithIds.put(R.id.edt_InOut_conveyance, 19);
        sViewsWithIds.put(R.id.meeting_type_layout, 20);
        sViewsWithIds.put(R.id.meetingTextView, 21);
        sViewsWithIds.put(R.id.spin_meeting_type, 22);
        sViewsWithIds.put(R.id.btn_spin_meeting_type, 23);
        sViewsWithIds.put(R.id.workTextView, 24);
        sViewsWithIds.put(R.id.spin_comp_status, 25);
        sViewsWithIds.put(R.id.btn_spin_work_type, 26);
        sViewsWithIds.put(R.id.chargesTextView, 27);
        sViewsWithIds.put(R.id.spin_charge_type, 28);
        sViewsWithIds.put(R.id.btn_spin_charges_type, 29);
        sViewsWithIds.put(R.id.btn_od_to_time, 30);
        sViewsWithIds.put(R.id.ncr_view_layout, 31);
        sViewsWithIds.put(R.id.edt_InOut_amount, 32);
        sViewsWithIds.put(R.id.txt_Document, 33);
        sViewsWithIds.put(R.id.lr_MedicalCertificateLayout, 34);
        sViewsWithIds.put(R.id.btn_TEuploadFile, 35);
        sViewsWithIds.put(R.id.add_multiple_file, 36);
        sViewsWithIds.put(R.id.txt_AddMore, 37);
        sViewsWithIds.put(R.id.non_ncr_view_layout, 38);
        sViewsWithIds.put(R.id.hotel_book_spinner, 39);
        sViewsWithIds.put(R.id.hotel_amount_etv, 40);
        sViewsWithIds.put(R.id.btn_hotel_uploadFile, 41);
        sViewsWithIds.put(R.id.flight_book_spinner, 42);
        sViewsWithIds.put(R.id.flight_amount_etv, 43);
        sViewsWithIds.put(R.id.btn_flight_uploadFile, 44);
        sViewsWithIds.put(R.id.train_book_spinner, 45);
        sViewsWithIds.put(R.id.train_amount_etv, 46);
        sViewsWithIds.put(R.id.btn_train_uploadFile, 47);
        sViewsWithIds.put(R.id.cab_type_layout, 48);
        sViewsWithIds.put(R.id.CabTextView, 49);
        sViewsWithIds.put(R.id.spin_cab_type, 50);
        sViewsWithIds.put(R.id.spin_dropdown_type, 51);
        sViewsWithIds.put(R.id.cab_amount, 52);
        sViewsWithIds.put(R.id.btn_cabuploadFile, 53);
        sViewsWithIds.put(R.id.other_layout, 54);
        sViewsWithIds.put(R.id.edt_InOut_other, 55);
        sViewsWithIds.put(R.id.btn_otheruploadFile, 56);
        sViewsWithIds.put(R.id.edt_InOut_remarks, 57);
        sViewsWithIds.put(R.id.btn_od_request_submit, 58);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public InOutRequestFragmentBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 59, sIncludes, sViewsWithIds));
    }
    private InOutRequestFragmentBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[49]
            , (androidx.recyclerview.widget.RecyclerView) bindings[36]
            , (android.widget.Button) bindings[53]
            , (android.widget.Button) bindings[44]
            , (android.widget.Button) bindings[41]
            , (android.widget.Button) bindings[3]
            , (android.widget.Button) bindings[58]
            , (android.widget.TextView) bindings[30]
            , (android.widget.Button) bindings[56]
            , (android.widget.Button) bindings[29]
            , (android.widget.Button) bindings[23]
            , (android.widget.Spinner) bindings[8]
            , (android.widget.Button) bindings[26]
            , (android.widget.Button) bindings[35]
            , (android.widget.Button) bindings[47]
            , (android.widget.EditText) bindings[52]
            , (android.widget.LinearLayout) bindings[48]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[27]
            , (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[1]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[5]
            , (android.widget.EditText) bindings[11]
            , (android.widget.EditText) bindings[32]
            , (android.widget.TextView) bindings[19]
            , (android.widget.EditText) bindings[13]
            , (android.widget.EditText) bindings[55]
            , (android.widget.EditText) bindings[57]
            , (android.widget.EditText) bindings[15]
            , (android.widget.EditText) bindings[43]
            , (android.widget.Spinner) bindings[42]
            , (android.widget.EditText) bindings[40]
            , (android.widget.Spinner) bindings[39]
            , (android.widget.LinearLayout) bindings[7]
            , (android.widget.LinearLayout) bindings[4]
            , (android.widget.LinearLayout) bindings[34]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[21]
            , (android.widget.LinearLayout) bindings[20]
            , (android.widget.RadioButton) bindings[17]
            , (android.widget.LinearLayout) bindings[31]
            , (android.widget.RadioButton) bindings[18]
            , (android.widget.LinearLayout) bindings[38]
            , (android.widget.LinearLayout) bindings[54]
            , (android.widget.RadioGroup) bindings[16]
            , (android.widget.Spinner) bindings[50]
            , (android.widget.Spinner) bindings[28]
            , (android.widget.Spinner) bindings[25]
            , (android.widget.Spinner) bindings[51]
            , (android.widget.Spinner) bindings[22]
            , (android.widget.Spinner) bindings[9]
            , (android.widget.Spinner) bindings[6]
            , (android.widget.EditText) bindings[46]
            , (android.widget.Spinner) bindings[45]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[37]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[33]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[10]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[2]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[12]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[14]
            , (com.savvy.hrmsnewapp.customwidgets.CustomTextView) bindings[24]
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