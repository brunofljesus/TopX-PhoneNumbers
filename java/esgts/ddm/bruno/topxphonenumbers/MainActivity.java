package esgts.ddm.bruno.topxphonenumbers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    Button mBtnEntrarTelefoneFavorito;
    EditText mEtEntrarTelefoneFavorito;

    Set<Long> mSetTelefonesFavoritos;

    final int NR_DIGITOS_TLM = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        mContext = MainActivity.this;
        mSetTelefonesFavoritos = new HashSet<Long>();

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
    }

    private boolean adicionarTelefoneFavorito(){
        String numero = mEtEntrarTelefoneFavorito.getText().toString();

        try {
            Long lnumero = Long.parseLong(numero);
            mSetTelefonesFavoritos.add(lnumero);
        } catch (Exception e){
            String strCorreuMal = e.getMessage().toString();
            makeToast(strCorreuMal);
            return false;
        }

        /*
         * TODO:
         *      -> Actualizar interface grafica
         */
        return true;
    }

    void makeToast(String msg) {
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
