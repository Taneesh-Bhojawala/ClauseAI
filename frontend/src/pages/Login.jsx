import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import {
    Box, Button, Container, TextField, Typography, Paper, Tabs, Tab,
    InputAdornment, IconButton, CircularProgress, Link
} from '@mui/material';
import { Visibility, VisibilityOff, Email, Lock, Person, Key } from '@mui/icons-material';

const API_URL = "http://localhost:8080/api/auth";

// --- Helper for Error Messages ---
const getErrorMessage = (error) => {
    if (error.response && error.response.data) {
        if (typeof error.response.data === 'string') return error.response.data;
        if (error.response.data.message) return error.response.data.message;
    }
    return "Something went wrong.";
};

// --- Styles ---
const darkInputStyles = {
    '& .MuiOutlinedInput-root': {
        color: 'white',
        '& fieldset': { borderColor: '#475569' },
        '&:hover fieldset': { borderColor: '#3b82f6' },
        '&.Mui-focused fieldset': { borderColor: '#3b82f6' },
    },
    '& .MuiInputLabel-root': { color: '#94a3b8' },
    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
    '& .MuiInputAdornment-root': { color: '#94a3b8' },
};

const Login = () => {
    const navigate = useNavigate();

    // Modes: 0=Login, 1=Signup, 2=ForgotPassword
    const [tabValue, setTabValue] = useState(0);
    const [showOtpField, setShowOtpField] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const [formData, setFormData] = useState({
        username: '', email: '', password: '', otp: ''
    });

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
        setShowOtpField(false);
        setFormData({ username: '', email: '', password: '', otp: '' });
    };

    // --- ACTIONS ---

    const handleGenerateOtp = async () => {
        if (!formData.email) return toast.error("Please enter email");
        setIsLoading(true);
        try {
            await axios.post(`${API_URL}/otp`, { email: formData.email });
            toast.success("OTP Sent!");
            setShowOtpField(true);
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally { setIsLoading(false); }
    };

    const handleLogin = async () => {
        setIsLoading(true);
        try {
            const res = await axios.post(`${API_URL}/login`, { email: formData.email, password: formData.password });
            localStorage.setItem("user", JSON.stringify(res.data));
            toast.success("Login Successful");
            navigate('/dashboard');
        } catch (error) { toast.error(getErrorMessage(error)); }
        finally { setIsLoading(false); }
    };

    const handleSignup = async () => {
        setIsLoading(true);
        try {
            await axios.post(`${API_URL}/signup`, { ...formData });
            toast.success("Account Created!");
            setTabValue(0); setShowOtpField(false);
        } catch (error) { toast.error(getErrorMessage(error)); }
        finally { setIsLoading(false); }
    };

    const handleResetPassword = async () => {
        if (!formData.otp || !formData.password) return toast.error("Fill OTP and New Password");
        setIsLoading(true);
        try {
            await axios.post(`${API_URL}/reset-password`, {
                email: formData.email, otp: formData.otp, newPassword: formData.password
            });
            toast.success("Password Reset! Please Login.");
            setTabValue(0); setShowOtpField(false);
        } catch (error) { toast.error(getErrorMessage(error)); }
        finally { setIsLoading(false); }
    };

    return (
        <Container maxWidth="xs">
            <Paper elevation={10} sx={{ p: 4, borderRadius: 4, bgcolor: '#1e293b', color: 'white', border: '1px solid #334155' }}>
                <Typography variant="h4" align="center" fontWeight="bold" sx={{ mb: 1, color: '#60a5fa' }}>ClauseAI</Typography>
                <Typography variant="body2" align="center" sx={{ color: '#94a3b8', mb: 3 }}>
                    {tabValue === 2 ? "Reset Your Password" : (tabValue === 0 ? "Welcome back, Lawyer." : "Analyze contracts in seconds.")}
                </Typography>

                {/* Hide Tabs when in Forgot Password Mode */}
                {tabValue !== 2 && (
                    <Tabs value={tabValue} onChange={handleTabChange} variant="fullWidth" sx={{ mb: 3, '& .MuiTab-root': { color: '#94a3b8' }, '& .Mui-selected': { color: '#60a5fa' }, '& .MuiTabs-indicator': { bgcolor: '#60a5fa' } }}>
                        <Tab label="Log In" />
                        <Tab label="Sign Up" />
                    </Tabs>
                )}

                <Box component="form" onSubmit={(e) => e.preventDefault()} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>

                    {/* Username (Signup Only) */}
                    {tabValue === 1 && (
                        <TextField fullWidth label="Username" name="username" value={formData.username} onChange={handleChange} disabled={showOtpField} sx={darkInputStyles} InputProps={{ startAdornment: (<InputAdornment position="start"><Person /></InputAdornment>) }} />
                    )}

                    {/* Email (Always Visible) */}
                    <TextField fullWidth label="Email Address" name="email" type="email" value={formData.email} onChange={handleChange} disabled={showOtpField && tabValue !== 2} sx={darkInputStyles} InputProps={{ startAdornment: (<InputAdornment position="start"><Email /></InputAdornment>) }} />

                    {/* Password Field (Login, Signup, Reset) */}
                    {/* In Forgot Password mode, this only shows AFTER OTP is sent (Step 2) OR if we want them to set new pass immediately. Let's show it in Step 2 for Reset. */}
                    {(tabValue !== 2 || showOtpField) && (
                        <TextField
                            fullWidth
                            label={tabValue === 2 ? "New Password" : "Password"}
                            name="password"
                            type={showPassword ? 'text' : 'password'}
                            value={formData.password}
                            onChange={handleChange}
                            disabled={showOtpField && tabValue === 1} // Disabled only during Signup OTP verification
                            sx={darkInputStyles}
                            InputProps={{
                                startAdornment: (<InputAdornment position="start"><Lock /></InputAdornment>),
                                endAdornment: (<InputAdornment position="end"><IconButton onClick={() => setShowPassword(!showPassword)} edge="end" sx={{ color: '#94a3b8' }}>{showPassword ? <VisibilityOff /> : <Visibility />}</IconButton></InputAdornment>)
                            }}
                        />
                    )}

                    {/* OTP Field (Signup Step 2 OR ForgotPassword Step 2) */}
                    {showOtpField && (
                        <TextField fullWidth label="Enter 6-Digit OTP" name="otp" value={formData.otp} onChange={handleChange} sx={{ ...darkInputStyles, '& .MuiOutlinedInput-root fieldset': { borderColor: '#22c55e' } }} InputProps={{ startAdornment: (<InputAdornment position="start"><Key sx={{ color: '#22c55e' }} /></InputAdornment>) }} />
                    )}

                    {/* Forgot Password Link (Login Only) */}
                    {tabValue === 0 && (
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Link component="button" variant="body2" onClick={() => { setTabValue(2); setShowOtpField(false); setFormData({username:'', email:'', password:'', otp:''}) }} sx={{ color: '#60a5fa', textDecoration: 'none' }}>
                                Forgot Password?
                            </Link>
                        </Box>
                    )}

                    {/* Back to Login Link (Forgot Password Mode Only) */}
                    {tabValue === 2 && (
                        <Box sx={{ display: 'flex', justifyContent: 'flex-start' }}>
                            <Link component="button" variant="body2" onClick={() => setTabValue(0)} sx={{ color: '#94a3b8', textDecoration: 'none' }}>
                                ← Back to Login
                            </Link>
                        </Box>
                    )}

                    {/* Main Action Button */}
                    <Button
                        fullWidth variant="contained" size="large" type="button" disabled={isLoading}
                        sx={{ mt: 1, py: 1.5, fontWeight: 'bold', borderRadius: 2, bgcolor: tabValue === 0 ? '#3b82f6' : (tabValue === 2 ? '#f59e0b' : (!showOtpField ? '#8b5cf6' : '#22c55e')) }}
                        onClick={() => {
                            if (tabValue === 0) handleLogin();
                            else if (tabValue === 1) !showOtpField ? handleGenerateOtp() : handleSignup();
                            else if (tabValue === 2) !showOtpField ? handleGenerateOtp() : handleResetPassword();
                        }}
                    >
                        {isLoading ? <CircularProgress size={24} color="inherit" /> : (
                            tabValue === 0 ? "Log In" : (
                                tabValue === 1 ? (!showOtpField ? "Generate OTP" : "Verify & Signup") :
                                    (!showOtpField ? "Send Reset OTP" : "Reset Password")
                            )
                        )}
                    </Button>

                </Box>
            </Paper>
        </Container>
    );
};

export default Login;