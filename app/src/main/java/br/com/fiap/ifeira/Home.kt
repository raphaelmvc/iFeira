package br.com.fiap.ifeira

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Home : ComponentActivity() {

    private lateinit var nomeUsuario: TextView
    private lateinit var emailUsuario: TextView
    private lateinit var btDeslogar: Button
    private lateinit var menu: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var db: FirebaseFirestore
    private lateinit var usuarioID: String
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        iniciarComponentes()

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> {
                    // Navegar para a tela de perfil
                    val intent = Intent(this, Perfil::class.java)
                    startActivity(intent)
                    true
                }
                // Outros itens de menu, se houver
                else -> false
            }
        }

        menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        usuarioID = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                nomeUsuario.text = documentSnapshot.getString("usuario")
                emailUsuario.text = FirebaseAuth.getInstance().currentUser?.email

                val headerView = navigationView.getHeaderView(0)
                val textViewNomeUsuario = headerView.findViewById<TextView>(R.id.textViewNomeUsuario)
                val textViewEmailUsuario = headerView.findViewById<TextView>(R.id.textViewEmailUsuario)

                textViewNomeUsuario.text = documentSnapshot.getString("usuario")
                textViewEmailUsuario.text = FirebaseAuth.getInstance().currentUser?.email
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                } else {
                    navController.navigateUp() || super.onOptionsItemSelected(item)
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun iniciarComponentes() {
        menu = findViewById(R.id.iconeMenu)
        nomeUsuario = findViewById(R.id.textnomeUsuario)
        emailUsuario = findViewById(R.id.textemailUsuario)
        btDeslogar = findViewById(R.id.bt_deslogar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
    }
}
