package com.rolledback.restaurantmap.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rolledback.restaurantmap.FragmentInterfaces.IAppLoginListener;
import com.rolledback.restaurantmap.Lib.AppState;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.AccountManager;

public class LoginFragment extends MainFragment {
    private IAppLoginListener _appLoginListener;

    private EditText _usernameView;
    private EditText _passwordView;
    private View _progressView;
    private View _loginFormView;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        _usernameView = (EditText) view.findViewById(R.id.username);
        _passwordView = (EditText) view.findViewById(R.id.password);
        Button mEmailSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        _loginFormView = view.findViewById(R.id.login_form);
        _progressView = view.findViewById(R.id.login_progress);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IAppLoginListener) {
            _appLoginListener = (IAppLoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        _usernameView.setError(null);
        _passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = _usernameView.getText().toString();
        String password = _passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            _passwordView.setError(getString(R.string.error_field_required));
            focusView = _passwordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            _usernameView.setError(getString(R.string.error_field_required));
            focusView = _usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d("LOGIN-ATTEMPT", email);
            Log.d("LOGIN-ATTEMPT", password);
            this.login(email, password);
        }
    }

    private void login(String username, String password) {
        showProgress(true);
        AccountManager.getInstance().login(this.getContext(), new AccountManager.ILoginListener() {
            @Override
            public void onLogin(boolean loginSuccessful) {
                showProgress(false);
                if (loginSuccessful) {
                    _appLoginListener.onLoginEvent();
                } else {
                    _passwordView.setError(getString(R.string.error_invalid_cred));
                    _passwordView.requestFocus();
                }
            }
        }, username, password);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        _loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        _loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                _loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        _progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public boolean shouldShowAddButton(AppState currState) {
        return false;
    }

    @Override
    public boolean shouldShowFilterButton(AppState currState) {
        return false;
    }
}
