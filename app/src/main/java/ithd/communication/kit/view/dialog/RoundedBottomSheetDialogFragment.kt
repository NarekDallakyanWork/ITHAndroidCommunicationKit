package ithd.communication.kit.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ithd.communication.kit.R
import kotlinx.android.synthetic.main.fragment_quantity_sheet.*

open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var onClick: ((quantityDialog: RoundedBottomSheetDialogFragment) -> Unit?)? =
        null
    private var productQuantityFromBasket: Int? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupDialogHeight(bottomSheetDialog)
        }
        return dialog
    }

    fun addListener(onClick: ((quantityDialog: RoundedBottomSheetDialogFragment) -> Unit?)?) {
        this.onClick = onClick
    }

    private fun setupDialogHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        val windowHeight = (quantityDialogButton.y).toInt() + ((quantityDialogButton.height) * 2)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.fragment_quantity_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        onClicked()
    }

    private fun onClicked() {

        quantityDialogButton.setOnClickListener {

            quantityProgress.visibility = View.VISIBLE
        }
    }

    private fun setUpUI() {

        quantityThumbnailImage.setCornerRadiiDP(
            10f, 10f, 10f, 10f
        )
    }

    fun hideProgress() {
        quantityProgress.visibility = View.GONE
    }
}