package esgts.ddm.bruno.topxphonenumbers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    Button mBtnEntrarTelefoneFavorito;
    EditText mEtEntrarTelefoneFavorito;

    Set<Long> mSetTelefonesFavoritos;

    View.OnClickListener mBtnCallClickHandler;
    LinearLayout mLlTelefonesFavoritos;

    final int NR_DIGITOS_TLM = 9;
    final int TELEFONES_FAVORITOS_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mContext = MainActivity.this;
        mSetTelefonesFavoritos = new HashSet<Long>();

        mLlTelefonesFavoritos = (LinearLayout) findViewById(R.id.id_llTelefonesFavoritosUtilizador);
        mBtnEntrarTelefoneFavorito = (Button) findViewById(R.id.id_btnEntrarTelefoneFavorito);
        mEtEntrarTelefoneFavorito = (EditText) findViewById(R.id.id_etEntrarTelefoneFavorito);

        /*
         * TODO:
         *      -> Tentar manter NO_SUGGESTIONS mas mesmo assim só permitir numeros
         */
        personalizarEditText(mEtEntrarTelefoneFavorito, NR_DIGITOS_TLM);

        mBtnEntrarTelefoneFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarTelefoneFavorito();
            }
        });

        mBtnCallClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                telefonarParaNumero(btn.getText().toString());
            }
        };

    }

    private boolean adicionarTelefoneFavorito() {
        String numero = mEtEntrarTelefoneFavorito.getText().toString();

        try {
            Long lnumero = Long.parseLong(numero);
            mSetTelefonesFavoritos.add(lnumero);
        } catch (Exception e) {
            String strCorreuMal = e.getMessage().toString();
            makeToast(strCorreuMal);
            return false;
        }

        atualizarButtonsParaTelefonar();
        return true;
    }

    void atualizarButtonsParaTelefonar(){
        Long[] numeros = mSetTelefonesFavoritos.toArray(
                new Long[mSetTelefonesFavoritos.toArray().length]
        );

        mLlTelefonesFavoritos.removeAllViews();
        for (Long n : numeros){
            adicionarBotao(n.toString(), mLlTelefonesFavoritos, mBtnCallClickHandler);
        }
    }
    boolean telefonarParaNumero(String strNumero) {
        if (pedirPermissao(Manifest.permission.CALL_PHONE) != true)
            makeToast("Pedido de permissao falhou");

        Uri uriNumero = Uri.parse("tel:".concat(strNumero));
        Intent telefonar = new Intent(Intent.ACTION_CALL);
        /*Confere uma activity stack propria a um componente*/
        telefonar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        telefonar.setData(uriNumero);

        try {
            startActivity(telefonar);
            return true;
        }catch(SecurityException e){
            makeToast("Sem permissões runtime para telefonar");
            return false;
        }
    }

    boolean pedirPermissao(String strPermissao /*A permissao que queremos pedir*/){
        /*Exemplos de permissoes:
            * Manifest.permission.*
         */
        int authorized = ContextCompat.checkSelfPermission(mContext,strPermissao); //verificar se ja foi autorizado

        boolean isAuthorized = authorized == PackageManager.PERMISSION_GRANTED;

        if (isAuthorized){
            //ja estava autorizada
            return true;
        }else{
            //pedir a autorizacao
            try {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{strPermissao},
                        TELEFONES_FAVORITOS_CODE);
                //TODO: implementar um callback
                return true; //fomos capazes de fazer um pedido
            }catch(Exception e){
                makeToast("Erro ao pedir permissoes: ".concat(e.getMessage().toString()));
                return false;
            }
        }

    }

    void adicionarBotao(String text, LinearLayout ll, View.OnClickListener listener){
        LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLayoutParams.setMargins(10, 10, 10, 10);

        Button btn;
        btn = new Button(mContext);
        int iBtnId = utilRandom(1,5000);
        btn.setId(iBtnId);
        btn.setText(text);

        btn.setLayoutParams(btnLayoutParams);

        btn.setOnClickListener(listener);
        ll.addView(btn);
    }

    int utilRandom(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    void makeToast(String msg, int duracao) {
        if ((duracao != Toast.LENGTH_LONG ) && (duracao != Toast.LENGTH_SHORT))
                return;

        Toast t = Toast.makeText(
                mContext, msg, duracao
        );
        t.show();
    }

    void makeToast(String msg){
        Toast t = Toast.makeText(
                mContext, msg, Toast.LENGTH_LONG
        );
        t.show();
    }

    private void personalizarEditText(EditText et, int iMaxDigitos) {
        //et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); //tudo
        //et.setInputType(InputType.TYPE_CLASS_PHONE); //numeros e virgulas
        et.setInputType(InputType.TYPE_CLASS_NUMBER); //numeros de 0 a 9

        InputFilter[] filtros = new InputFilter[1];
        filtros[0] = new InputFilter.LengthFilter(iMaxDigitos);
        et.setFilters(filtros);
    }
}
