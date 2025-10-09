import React, { useState } from "react";
import "../styles/auth.css"; // import your CSS

import { registerUser, loginUser } from "../api";
import { useNavigate } from "react-router-dom";

export default function AuthPage() {
    const [isSignUp, setIsSignUp] = useState(false);
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [loginError, setLoginError] = useState(""); // login error message
    const [successMessage, setSuccessMessage] = useState("");
    const navigate = useNavigate();

    const toggle = () => {
        setIsSignUp(!isSignUp);
        setPasswordError("");
        setLoginError(""); // clear errors when switching
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            setPasswordError("Passwordet nuk perputhen");
            return;
        } else {
            setPasswordError("");
        }

        try {
            await registerUser({ username, email: email.toLowerCase(), password });

            setSuccessMessage("Regjistrimi u krye me sukses!");
            setPasswordError("");

            setTimeout(() => {
                setSuccessMessage("");
                setIsSignUp(false);
            }, 3000);
        } catch (err) {
            setPasswordError(err.response?.data || "Regjistrimi deshtoi ");
        }
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            await loginUser({ email: email.toLowerCase(), password });
            //for testing
            window.location.href = "/Chat.js";
        } catch (err) {
            setLoginError(err.response?.data ||"Kredencialet janë vendosur gabim");
        }
    };

    return (
        <div id="container" className={`container ${isSignUp ? "sign-up" : "sign-in"}`}>
            {/* FORM SECTION */}
            <div className="row">
                {/* SIGN UP */}
                <div className="col align-items-center flex-col sign-up">
                    <div className="form-wrapper align-items-center">
                        <form className="form sign-up" onSubmit={handleRegister}>
                            <h3 style={{ marginBottom: "1.2rem", color: "#4EA685", fontWeight: "600", textAlign: "start" }}>
                                Regjistrohuni
                            </h3>
                            <div className="input-group">
                                <i className="bx bxs-user"></i>
                                <input
                                    type="text"
                                    placeholder="Username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="input-group">
                                <i className="bx bx-mail-send"></i>
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="input-group">
                                <i className="bx bxs-lock-alt"></i>
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="input-group">
                                <i className="bx bxs-lock-alt"></i>
                                <input
                                    type="password"
                                    placeholder="Confirm Password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    required
                                />
                            </div>
                            {passwordError && (
                                <p style={{ color: "red", fontSize: "0.8rem", marginTop: "0.3rem" }}>
                                    {passwordError}
                                </p>
                            )}
                            <button type="submit">Sign up</button>
                            {successMessage && (
                                <p style={{ color: "green", fontSize: "0.8rem", marginTop: "0.3rem" }}>
                                    {successMessage}
                                </p>
                            )}
                            <p>
                                <span>Keni një account ? </span>
                                <b onClick={toggle} className="pointer">
                                    Sign in
                                </b>
                            </p>
                        </form>
                    </div>
                </div>

                {/* SIGN IN */}
                <div className="col align-items-center flex-col sign-in">
                    <div className="form-wrapper align-items-center">
                        <form className="form sign-in" onSubmit={handleLogin}>
                            <h3 style={{ marginBottom: "1.2rem", color: "#4EA685", fontWeight: "600", textAlign: "start" }}>
                                Plotësoni kredencialet tuaja
                            </h3>
                            <div className="input-group">
                                <i className="bx bxs-user"></i>
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="input-group">
                                <i className="bx bxs-lock-alt"></i>
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            {loginError && (
                                <p style={{ color: "red", fontSize: "0.8rem", marginTop: "0.3rem" }}>
                                    {loginError}
                                </p>
                            )}
                            <button type="submit">Sign in</button>
                            <p>
                                <span>Nuk keni një account ?</span>
                                <b onClick={toggle} className="pointer">
                                    Sign up
                                </b>
                            </p>
                        </form>
                    </div>
                </div>
            </div>

            {/* CONTENT SECTION */}
            <div className="row content-row">
                <div className="col align-items-center flex-col">
                    <div className="text sign-in">
                        <h2>MirëSeVini në Chathub !</h2>

                            <div className="img sign-in">
                                <img src="/social-media.png" alt="Social Media" />
                            </div>

                    </div>
                   {/* <div className="img sign-in"></div>*/}
                </div>
                <div className="col align-items-center flex-col">
                    <div className="img sign-up">
                        <img src="/social-media.png" alt="Social Media" />
                    </div>
                    <div className="text sign-up">
                        <h2>Bashkohu me ne </h2>
                    </div>
                </div>
            </div>
        </div>
    );
}
