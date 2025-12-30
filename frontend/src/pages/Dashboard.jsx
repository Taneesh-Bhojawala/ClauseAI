import React, { useEffect, useState } from 'react';
import { Container, Grid, Paper, Typography, Button, CircularProgress, Chip, Box } from '@mui/material';
import { Add } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import ContractService from '../services/contract.service';
import toast from 'react-hot-toast';

const Dashboard = () => {
    const navigate = useNavigate();
    const [history, setHistory] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const historyRes = await ContractService.getHistory();
                setHistory(historyRes.data);
                const statsRes = await ContractService.getDashboardStats();
                setStats(statsRes.data);
            } catch (error) {
                console.error(error);
                toast.error("Failed to load dashboard data");
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Grid container spacing={3} alignItems="center" sx={{ mb: 4 }}>
                <Grid item xs={12} md={8}>
                    <Typography variant="h4" fontWeight="bold">Dashboard</Typography>
                    <Typography color="text.secondary">Welcome back, {stats?.username || "Lawyer"}.</Typography>
                </Grid>
                <Grid item xs={12} md={4} sx={{ textAlign: 'right' }}>
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => navigate('/analyze')}
                        sx={{ bgcolor: '#3b82f6', py: 1.5 }}
                    >
                        Analyze New Contract
                    </Button>
                </Grid>
            </Grid>

            {/* Stats Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, bgcolor: '#1e293b', color: 'white', borderRadius: 2 }}>
                        <Typography variant="h6" color="#94a3b8">Total Analyzed</Typography>
                        <Typography variant="h3" color="#60a5fa">{stats?.totalContractsAnalyzed || 0}</Typography>
                    </Paper>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3, bgcolor: '#1e293b', color: 'white', borderRadius: 2 }}>
                        <Typography variant="h6" color="#94a3b8">Avg Risk Score</Typography>
                        <Typography variant="h3" color={stats?.averageRiskScore > 70 ? "#ef4444" : "#22c55e"}>
                            {stats?.averageRiskScore || 0}
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>

            {/* History Table */}
            <Typography variant="h5" sx={{ mb: 2, fontWeight: 'bold' }}>Recent Activity</Typography>
            {loading ? <CircularProgress /> : (
                <Grid container spacing={2}>
                    {history.length === 0 ? (
                        <Typography sx={{ ml: 2, color: 'gray' }}>No contracts analyzed yet.</Typography>
                    ) : (
                        history.map((contract) => (
                            <Grid item xs={12} key={contract.id}>
                                <Paper sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', bgcolor: '#1e293b', color: 'white', border: '1px solid #334155' }}>
                                    <Box>
                                        <Typography variant="h6">{contract.title || "Untitled Contract"}</Typography>
                                        <Typography variant="body2" color="#94a3b8">
                                            {new Date(contract.uploadDateTime).toLocaleDateString()}
                                        </Typography>
                                    </Box>
                                    <Chip
                                        label={`Risk: ${contract.overallRiskScore}/100`}
                                        sx={{
                                            bgcolor: contract.overallRiskScore > 70 ? 'rgba(239, 68, 68, 0.2)' : 'rgba(34, 197, 94, 0.2)',
                                            color: contract.overallRiskScore > 70 ? '#ef4444' : '#22c55e',
                                            fontWeight: 'bold'
                                        }}
                                    />
                                </Paper>
                            </Grid>
                        ))
                    )}
                </Grid>
            )}
        </Container>
    );
};

export default Dashboard;