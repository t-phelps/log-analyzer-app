import "./App.css";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import { LoginForm } from "./components/LoginForm";
import { CreateAccountForm } from "./components/CreateAccountForm";
import { LandingPage } from "./components/LandingPage";
import { useState } from "react";
import { NavBar } from "./components/NavBar";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginForm setIsLoggedIn={setIsLoggedIn} />} />
        <Route
          path="/create-account"
          element={<CreateAccountForm setIsLoggedIn={setIsLoggedIn} />}
        />
        <Route path="/landing" element={<LandingPage />} />
        {/* 
        <Route path="/" element={<LoginForm />} />
        <Route path="/create-account" element={<CreateAccountForm />} />
        {/* <Route path="/" element={<LandingPage />} /> */}
      </Routes>
    </Router>
  );
}

export default App;
