import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Analysis from './pages/Analysis';
import Navbar from './components/Navbar';

const PrivateRoute = ({ children }) => {
    const user = localStorage.getItem("user");
    return user ? children : <Navigate to="/" />;
};

function App() {
    return (
        <Router>
            <Toaster position="top-right" />
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/dashboard" element={
                    <PrivateRoute><Navbar /><Dashboard /></PrivateRoute>
                } />
                <Route path="/analyze" element={
                    <PrivateRoute><Navbar /><Analysis /></PrivateRoute>
                } />
            </Routes>
        </Router>
    );
}

export default App;