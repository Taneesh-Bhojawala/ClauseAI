import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';

const Navbar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        AuthService.logout();
        navigate('/');
    };

    return (
        <AppBar position="static" sx={{ backgroundColor: '#1e293b' }}>
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1, color: '#60a5fa', fontWeight: 'bold', cursor:'pointer' }} onClick={() => navigate('/dashboard')}>
                    ClauseAI
                </Typography>
                <Box>
                    <Button color="inherit" onClick={() => navigate('/dashboard')}>Dashboard</Button>
                    <Button color="inherit" onClick={() => navigate('/analyze')}>New Analysis</Button>
                    <Button color="error" onClick={handleLogout} sx={{ ml: 2 }}>Logout</Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;