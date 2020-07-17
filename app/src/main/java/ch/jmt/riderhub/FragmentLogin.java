package ch.jmt.riderhub;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentLogin extends Fragment {

    // interface for clickevent handling
    public interface fragmentBtnSelected{
        public void onLoginBtnSelected();
    }

    private fragmentBtnSelected listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container,false);
        Button loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoginBtnSelected();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof fragmentBtnSelected){
                listener = (fragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

}
