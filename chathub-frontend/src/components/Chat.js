import React from "react";

const HomePage = () => {
    const handleLogout = async () => {
        try {
            // Call the Spring Boot logout endpoint
            const response = await fetch("/auth/logout", {
                method: "POST", // Spring Security default expects POST
                credentials: "include", // include cookies if using session-based auth
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                // Redirect to login page after logout
                window.location.href = "/AuthPage.js";
            } else {
                console.error("Logout failed");
            }
        } catch (error) {
            console.error("Error logging out:", error);
        }
    };

    return (
        <div>
            <h1>Hello, World!</h1>
            <p>Welcome to my first HTML page. This is a simple example showing how to display text on a webpage.</p>
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default HomePage;
