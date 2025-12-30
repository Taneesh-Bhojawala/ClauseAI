import {useState} from "react";
import {TextField, Button, Container, Paper, Typography, Box, Link, Alert, CircularProgress} from "@mui/material";
import api from "../services/api";
import {useNavigate} from "react-router-dom";

const Login = () => {
    const navigate = useNavigate();

    const [isSignup, setIsSignup] = useState(false);

    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [otp, setOtp] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({type: "", text: ""});
    const [otpSent, setOtpSent] = useState(false);

    const handleSendOtp = async () => {
        if(!email) return message({type: "error", message: "Please enter email first!"});

        setLoading(true);
        setMessage({type: "", text: ""});

        try
        {
            const response = await api.post("/auth/otp", {email});
            setMessage({type: "success", text: response.data.message});
            setOtpSent(true);
        }
        catch (error) {
            const errorMessage = error.response?.data?.message || "Failed to send OTP.";
            setMessage({type: "error", text: errorMessage});
        }
        finally {
            setLoading(false);
        }
    };

    const handleSubmit = async () => {
        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            if (isSignup) {

                await api.post('/auth/signup', { username, email, password, otp });

                setMessage({ type: 'success', text: 'Account created successfully! Please login.' });

                setIsSignup(false);
                setOtpSent(false);
                setOtp('');
                setPassword('');
            }
            else {

                const response = await api.post('/auth/login', { email, password });

                localStorage.setItem('token', response.data.token);
                localStorage.setItem('user', JSON.stringify(response.data));

                navigate('/dashboard');
            }
        }
        catch (error) {
            const errorMsg = error.response?.data?.message || 'Something went wrong. Please try again.';
            setMessage({ type: 'error', text: errorMsg });
        }
        finally {
            setLoading(false);
        }
    };

return (
    <Container maxWidth="xs">
        <Box mt={8}>
            <Paper elevation={3} style={{ padding: '2rem', textAlign: 'center' }}>

                {/* Header */}
                <Typography variant="h5" gutterBottom style={{ fontWeight: 'bold' }}>
                    {isSignup ? "Create Account" : "Welcome Back"}
                </Typography>
                <Typography variant="body2" color="textSecondary" gutterBottom>
                    {isSignup ? "Sign up to analyze contracts" : "Login to access your dashboard"}
                </Typography>

                {message.text && (
                    <Box mt={2} mb={2}>
                        <Alert severity={message.type}>{message.text}</Alert>
                    </Box>
                )}

                {isSignup && (
                    <TextField
                        label="Username" fullWidth margin="normal"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                )}

                <TextField
                    label="Email Address" fullWidth margin="normal"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                {isSignup && (
                    <Box display="flex" flexDirection="column" alignItems="center" mt={1}>
                        {!otpSent ? (
                            <Button
                                variant="outlined" size="small"
                                onClick={handleSendOtp}
                                disabled={loading || !email}
                                style={{ marginBottom: '0.5rem' }}
                            >
                                {loading ? "Sending..." : "Get OTP Code"}
                            </Button>
                        ) : (
                            <TextField
                                label="Enter 6-digit OTP" fullWidth margin="normal"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                helperText="OTP sent to your email"
                            />
                        )}
                    </Box>
                )}

                <TextField
                    label="Password" type="password" fullWidth margin="normal"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <Button
                    variant="contained" color="primary" fullWidth
                    onClick={handleSubmit}
                    disabled={loading}
                    style={{ marginTop: '1.5rem', padding: '0.75rem', fontWeight: 'bold' }}
                >
                    {loading ? <CircularProgress size={24} color="inherit"/> : (isSignup ? "Sign Up" : "Login")}
                </Button>

                <Box mt={3}>
                    <Typography variant="body2">
                        {isSignup ? "Already have an account? " : "Don't have an account? "}
                        <Link
                            component="button"
                            variant="body2"
                            style={{ fontWeight: 'bold' }}
                            onClick={() => {
                                setIsSignup(!isSignup);
                                setMessage({ type: '', text: '' }); // Clear errors when switching
                            }}
                        >
                            {isSignup ? "Login" : "Sign Up"}
                        </Link>
                    </Typography>
                </Box>

            </Paper>
        </Box>
    </Container>
);
};

export default Login;