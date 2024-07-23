package com.example.remitconnect.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContract
import com.example.remitconnect.R
import com.example.remitconnect.RemitConnectApp.Companion.appContext
import com.example.remitconnect.data.model.Contact

class ContactPicker : ActivityResultContract<Unit, Contact?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Contact? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            val contactUri: Uri? = intent.data
            contactUri?.let {
                val cursor = appContext.contentResolver.query(it, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    val id = cursor.getString(idIndex)
                    val displayName = cursor.getString(nameIndex)
                    val nameParts = displayName.split(" ")
                    val firstName = nameParts.firstOrNull() ?: ""
                    val lastName = nameParts.drop(1).joinToString(" ")

                    val phoneCursor = appContext.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (phoneCursor != null && phoneCursor.moveToFirst()) {
                        val phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phone = phoneCursor.getString(phoneIndex)
                        phoneCursor.close()

                        cursor.close()
                        return Contact(firstName, lastName, phone, R.drawable.ic_contact_placeholder)
                    }
                    phoneCursor?.close()
                }
                cursor?.close()
            }
        }
        return null
    }
}