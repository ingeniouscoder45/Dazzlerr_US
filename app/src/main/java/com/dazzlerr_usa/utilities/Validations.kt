package com.dazzlerr_usa.utilities

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.util.Patterns
import android.view.View

class Validations
{

    //----------------Register validations---------------------------------
    fun userRegisterValidate(context: Context, username: String, email: String, pass: String, confirmPass: String): Boolean {
        if (username.length == 0 && email.length == 0 && pass.length == 0 && confirmPass.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill all fields..", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (username.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill username field..", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (email.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill email field..", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Invalid email address", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show()
            return false

        } else if (pass.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill password field..", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (pass.length < 5) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Password should be atleast 5 digits long..", Snackbar.LENGTH_SHORT).show()
            return false

        } else {
            return true
        }
    }

    //----------------Login validations---------------------------------
    fun loginValidate(context: Context?, email: String, pass: String): Boolean {
        if (email.length == 0 && pass.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill all fields.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (email.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill email field.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Invalid email address.", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show()
            return false

        } else if (pass.length == 0)
        {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill password field.", Snackbar.LENGTH_SHORT).show()
            return false

        }
        else if (pass.length < 5)
        {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Password should be atleast 5 digits long.", Snackbar.LENGTH_SHORT).show()
            return false

        } else {
            return true
        }
    }

    fun verifyAccountValidate(context: Context?, emailOrPhone: String, type: String?): Boolean {
        if (type.equals("mobile", ignoreCase = true)) {
            if (emailOrPhone.length == 0) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter your phone number.", Snackbar.LENGTH_SHORT).show()
                return false

            } else if (!Patterns.PHONE.matcher(emailOrPhone).matches()) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter a valid phone number.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show()
                return false

            } else
                return true
        } else {
            if (emailOrPhone.length == 0) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter your email id.", Snackbar.LENGTH_SHORT).show()
                return false

            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter a valid email id.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show()
                return false

            } else
                return true

        }

    }

    //----------------Login validations---------------------------------
    fun restPasswordValidate(context: Context?, newPassword: String, confirmPassword: String): Boolean {
        if (newPassword.length == 0 && confirmPassword.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill all the fields.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (newPassword.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please enter your new password.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (confirmPassword.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please confirm your new password.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (newPassword.length < 5) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Password should be atleast 5 digits long.", Snackbar.LENGTH_SHORT).show()
            return false

        } else if (!newPassword.equals(confirmPassword, ignoreCase = true)) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Password and confirm password doesn't matched.", Snackbar.LENGTH_SHORT).show()
            return false

        } else {
            return true
        }
    }

}
