package ru.sikuda.mobile.todo_frag

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import ru.sikuda.mobile.todo_frag.databinding.ActivityMainBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val model: MainModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navigationHost.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.delete_all) {
            confirmDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all Data?")
        builder.setPositiveButton(
            "Yes"
        ) { _, _ ->
            model.deleteAllNotes()
            val navController = findNavController(R.id.nav_host_fragment_content_main)

            if(navController.currentDestination?.id == R.id.ListFragment)
                navController.navigate(R.id.action_ListFragment_to_ListFragment)
            else
                navController.navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }
        builder.setNegativeButton(
            "No"
        ) { _, _ -> }
        builder.create().show()
    }

//    //Permission for foto
//    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//        when {
//            granted -> {
////                lifecycleScope.launchWhenStarted {
////                    getTmpFileUri().let { uri ->
////                        latestTmpUri = uri
////                        takeImageResult.launch(uri)
////                    }
////                }
//                // user granted permission
//                //cameraShot.launch(null)
//            }
//            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
//                // user denied permission and set Don't ask again.
//                showSettingsDialog()
//            }
//            else -> {
//                showToast(R.string.denied_toast)
//            }
//        }
//    }
//
//    private fun showSettingsDialog() {
//        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
//        showToast(R.string.denied_toast)
//    }
//
//    private fun showToast(textId: Int) {
//        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun getTmpFileUri(): Uri {
//        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
//            createNewFile()
//            deleteOnExit()
//        }
//
//        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
//    }

}