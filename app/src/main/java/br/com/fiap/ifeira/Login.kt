package br.com.fiap.ifeira

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class Login : AppCompatActivity() {

    private lateinit var textCadastro: TextView
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btLogin: Button
    private lateinit var progressBar: ProgressBar
    private val mensagens = arrayOf("Preencher todos os campos", "Login efetuado com sucesso!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        iniciarComponentes()

        textCadastro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        btLogin.setOnClickListener {
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                exibirSnackbar(mensagens[0])
            } else {
                autenticarUsuario(email, senha)
            }
        }
    }

    private fun autenticarUsuario(email: String, senha: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.VISIBLE
                    Handler().postDelayed({
                        homePage()
                    }, 3000)
                } else {
                    var erro = "Erro ao logar usuário"

                    // Tratamento de exceções específicas do Firebase Authentication
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            erro = "O email ou senha estão invalidos!"
                        }
                        else -> {
                            task.exception?.printStackTrace()
                        }
                    }

                    exibirSnackbar(erro)
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            homePage()
        }
    }
    private fun homePage() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun iniciarComponentes() {
        textCadastro = findViewById(R.id.text_Cadastro)
        editEmail = findViewById(R.id.edit_email)
        editSenha = findViewById(R.id.edit_senha)
        btLogin = findViewById(R.id.bt_entrar)
        progressBar = findViewById(R.id.progressbar)
    }

    private fun exibirSnackbar(mensagem: String) {
        val snackbar = Snackbar.make(btLogin, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.WHITE)
        snackbar.setTextColor(Color.BLACK)
        snackbar.show()
    }
}
