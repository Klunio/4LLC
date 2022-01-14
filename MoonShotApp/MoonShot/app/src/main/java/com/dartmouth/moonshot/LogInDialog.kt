package com.dartmouth.moonshot

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

class LogInDialog : DialogFragment(), DialogInterface.OnClickListener {
    companion object{
        const val DIALOG_KEY = "dialog"
        const val LOGIN_INT = 0
        const val SIGNUP_INT = 1
        const val NO_EMAIL_PASSWORD_INT = 2
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        if (dialogId == LOGIN_INT) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(R.layout.log_in_dialog, null)
            builder.setView(view)
            builder.setPositiveButton("ok", this)
            ret = builder.create()
        } else if(dialogId == SIGNUP_INT) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(R.layout.sign_up_dialog, null)
            builder.setView(view)
            builder.setPositiveButton("ok", this)
            ret = builder.create()
        } else if(dialogId == NO_EMAIL_PASSWORD_INT) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(R.layout.no_email_password_dialog, null)
            builder.setView(view)
            builder.setPositiveButton("ok", this)
            ret = builder.create()
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {

    }
}