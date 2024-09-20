package com.payfort.fortapisimulator.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.payfort.fortpaymentsdk.callbacks.PayFortCallback
import com.payfort.fortpaymentsdk.domain.model.FortRequest
import com.payfort.fortpaymentsdk.utils.gone
import com.payfort.fortpaymentsdk.utils.visible
import com.payfort.fortpaymentsdk.views.model.PayComponents
import com.payfort.forttestapp.databinding.BottomSheetCustomuiBinding

class CustomUiDialog : DialogFragment(), PayFortCallback {

    private var _binding: BottomSheetCustomuiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var gson = Gson()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): CustomUiDialog {
            val fragment = CustomUiDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            val window = it.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCustomuiBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        val fortRequest: FortRequest =
            requireArguments().getSerializable("fortRequest") as FortRequest
        val environment = requireArguments().getString("env", "")
        val payComponents = PayComponents(
            binding.etCardNumberView,
            cvvView = binding.etCardCvv,
            binding.etCardExpiry,
            holderNameView = binding.cardHolderNameView
        )
        binding.btnPay.setup(environment!!, fortRequest, payComponents, this)
    }

    override fun startLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.visible()
        enableFields(false)
    }


    override fun onSuccess(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }

    override fun onFailure(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }

    private fun openResponsePage(responseString: String) {
        Log.e("Error", responseString)
        stopLoading()
        val openResponseActivityIntent = Intent(requireContext(), ResponseActivity::class.java)
        openResponseActivityIntent.putExtra("responseString", responseString)
        startActivity(openResponseActivityIntent)
    }

    private fun stopLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.gone()
        enableFields(true)
    }

    private fun enableFields(enableFields: Boolean) {
        with(binding) {
            cardHolderNameView.isEnabled = enableFields
            etCardNumberView.isEnabled = enableFields
            etCardExpiry.isEnabled = enableFields
            etCardCvv.isEnabled = enableFields
            btnPay.isEnabled = enableFields
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}