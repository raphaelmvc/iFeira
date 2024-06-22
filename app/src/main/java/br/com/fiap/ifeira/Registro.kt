package br.com.fiap.ifeira

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class Registro : AppCompatActivity() {

    private lateinit var edit_usuario: EditText
    private lateinit var edit_email: EditText
    private lateinit var edit_senha: EditText
    private lateinit var edit_confirmasenha: EditText
    private lateinit var bt_cadastrar: Button
    private val mensagens = arrayOf("Preencher todos os campos", "Cadastro realizado com sucesso")
    private lateinit var usuarioID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializa os componentes da UI
        IniciarComponentes()

        // Configuração do clique no TextView "Já tem cadastro? Faça login"
        val registro4TextView = findViewById<TextView>(R.id.registro4)
        registro4TextView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Configuração do clique no botão cadastrar
        bt_cadastrar.setOnClickListener { view ->
            val usuario = edit_usuario.text.toString()
            val email = edit_email.text.toString()
            val senha = edit_senha.text.toString()
            val confirmasenha = edit_confirmasenha.text.toString()

            if (usuario.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmasenha.isEmpty()) {
                exibirSnackbar(mensagens[0])
            } else {
                CadastrarUsuario(email, senha, confirmasenha)
            }
        }
    }

    // Cria o usuário com email e senha no Firebase Authentication
    private fun CadastrarUsuario(email: String, senha: String, confirmarSenha: String) {
        if (senha != confirmarSenha) {
            exibirSnackbar("As senhas não coincidem")
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    SalvarDadosUsuario();
                    exibirSnackbar(mensagens[1])
                } else {
                    var erro = "Erro ao cadastrar usuário"

                    // Tratamento de exceções específicas do Firebase Authentication
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        erro = "Digite uma senha com no mínimo 6 caracteres"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        erro = "Esse e-mail já está cadastrado"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        erro = "Credenciais inválidas"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    exibirSnackbar(erro)
                }
            }
    }

    // Método para iniciar os Componentes
    private fun IniciarComponentes() {
        edit_usuario = findViewById(R.id.edit_usuario)
        edit_email = findViewById(R.id.edit_email)
        edit_senha = findViewById(R.id.edit_senha)
        edit_confirmasenha = findViewById(R.id.edit_confirmasenha)
        bt_cadastrar = findViewById(R.id.bt_cadastrar)
    }


    private fun SalvarDadosUsuario() {
        val usuario = edit_usuario.text.toString()
        val db = FirebaseFirestore.getInstance()

        val usuarios = hashMapOf<String, Any>()
        usuarios["usuario"] = usuario

        val usuarioID = FirebaseAuth.getInstance().currentUser?.uid

        if (usuarioID != null) {
            val documentReference = db.collection("Usuarios").document(usuarioID)
            documentReference.set(usuarios)
                .addOnSuccessListener {
                    Log.d("db", "Sucesso ao salvar os dados")
                }
                .addOnFailureListener { e ->
                    Log.d("db_error", "Erro ao salvar os dados: ${e.message}")
                }
        } else {
            Log.d("db_error", "Usuário não autenticado")
        }
    }

    // Método para exibir Snackbar com mensagem
    private fun exibirSnackbar(mensagem: String) {
        val snackbar = Snackbar.make(bt_cadastrar, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.WHITE)
        snackbar.setTextColor(Color.BLACK)
        snackbar.show()
    }
}
