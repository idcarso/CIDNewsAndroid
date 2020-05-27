package com.amco.cidnews.Utilities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.VideoView;

import androidx.fragment.app.DialogFragment;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.R;

public class MyDialogFragment extends androidx.fragment.app.DialogFragment {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    private Button bntCerrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Do all the stuff to initialize your custom view
        // HACE LO NECESARIO PARA INICIALIZAR LA VISTA PERSONALIZADA
        View v = inflater.inflate(R.layout.dialog_home, container, false);

        bntCerrar =  v.findViewById(R.id.btn_aceptar);

        bntCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        VideoView videoView =(VideoView) v.findViewById(R.id.videos);

        videoView.setVideoURI(Uri.parse("android.resource://com.amco.cidnews/"+ R.raw.videostart));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.start();
        videoView.seekTo(1);
        videoView.requestFocus();

        return v;
    }

}