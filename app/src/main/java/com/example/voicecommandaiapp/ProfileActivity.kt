package com.example.voicecommandaiapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.voicecommandaiapp.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: CircleImageView
    private lateinit var etUserName: EditText
    private lateinit var sessionManager: SessionManager

    /* ---------------- PERMISSIONS ---------------- */

    private val requestCameraPermission = 
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera()
        }

    private val requestGalleryPermission = 
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openGallery()
        }

    private val takePicture = 
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                profileImageView.setImageBitmap(it)
                // (Optional) save bitmap to file if needed later
            }
        }

    private val selectImageFromGallery = 
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                profileImageView.setImageURI(it)
                saveProfileImage(it)
            }
        }

    /* ---------------- LIFECYCLE ---------------- */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        profileImageView = findViewById(R.id.iv_profile_avatar)
        etUserName = findViewById(R.id.et_name)

        // Back
        findViewById<ImageView>(R.id.iv_back_arrow).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Camera icon
        findViewById<ImageView>(R.id.iv_camera_icon).setOnClickListener {
            showBottomSheet()
        }

        // Load saved data
        loadProfileImage()
        loadUserName()
    }

    /* ---------------- BOTTOM SHEET ---------------- */

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_change_photo, null)
        dialog.setContentView(view)

        view.findViewById<ImageView>(R.id.iv_close_sheet).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.option_take_photo).setOnClickListener {
            dialog.dismiss()
            checkCameraPermissionAndOpen()
        }

        view.findViewById<LinearLayout>(R.id.option_choose_gallery).setOnClickListener {
            dialog.dismiss()
            checkGalleryPermissionAndOpen()
        }

        view.findViewById<LinearLayout>(R.id.option_remove_photo).setOnClickListener {
            dialog.dismiss()
            removeProfileImage()
        }

        dialog.show()
    }

    /* ---------------- PERMISSION HELPERS ---------------- */

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> openCamera()

            else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkGalleryPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> openGallery()

            else -> requestGalleryPermission.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun openCamera() {
        takePicture.launch(null)
    }

    private fun openGallery() {
        selectImageFromGallery.launch("image/*")
    }

    /* ---------------- PROFILE IMAGE ---------------- */

    private fun saveProfileImage(uri: Uri) {
        val prefs =
            getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image_uri", uri.toString()).apply()
    }

    private fun loadProfileImage() {
        val prefs =
            getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
        val uriString = prefs.getString("profile_image_uri", null)
        if (uriString != null) {
            profileImageView.setImageURI(Uri.parse(uriString))
        }
    }

    private fun removeProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_user_profile)
        val prefs =
            getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("profile_image_uri").apply()
    }

    /* ---------------- USER NAME ---------------- */

    private fun loadUserName() {
        etUserName.setText(sessionManager.userName)
    }

    override fun onPause() {
        super.onPause()
        // ✅ Save name when user leaves screen
        sessionManager.saveUser(sessionManager.userId, etUserName.text.toString().trim())
    }
}
