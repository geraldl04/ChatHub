import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import AuthPage from "./components/AuthPage";

function App() {
    return (
        <Router>
            <AuthPage />
        </Router>
    );
}

export default App;
