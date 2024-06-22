package br.com.fiap.ifeira

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.drawerlayout.widget.DrawerLayout
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        iniciarComponentes()

        // Configurar o clique no ícone do menu
        menu.setOnClickListener {
            // Aqui você pode abrir o menu, seja um DrawerLayout, PopupMenu, BottomSheet, etc.
            // Exemplo básico usando um Toast
            drawerLayout.openDrawer(navigationView, true)
        }

        // Configurar o clique no botão de deslogar
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
        val documentReference: DocumentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                nomeUsuario.text = documentSnapshot.getString("usuario")
                emailUsuario.text = FirebaseAuth.getInstance().currentUser?.email
            }
        }
    }

    private fun iniciarComponentes() {
        menu = findViewById(R.id.iconeMenu)
        nomeUsuario = findViewById(R.id.textnomeUsuario)
        emailUsuario = findViewById(R.id.textemailUsuario)
        btDeslogar = findViewById(R.id.bt_deslogar)
        db = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
    }
}