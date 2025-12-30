import React, { useState } from 'react';
import { Container, Grid, Paper, Typography, Button, TextField, Box, CircularProgress, Divider, Alert } from '@mui/material';
import { UploadFile } from '@mui/icons-material';
import toast from 'react-hot-toast';
import ContractService from '../services/contract.service';

const Analysis = () => {
    const [text, setText] = useState("");
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);

    const handleTextAnalyze = async () => {
        if (!text) return toast.error("Please enter text");
        setLoading(true);
        try {
            const res = await ContractService.analyzeText(text, "Manual Text Entry");
            setResult(res.data);
            toast.success("Analysis Complete!");
        } catch (err) {
            // Safe error handling
            const msg = err.response?.data?.message || err.response?.data || "Analysis Failed";
            toast.error(typeof msg === 'string' ? msg : "Analysis Failed");
        } finally {
            setLoading(false);
        }
    };

    const handleFileUpload = async (e) => {
        const uploadedFile = e.target.files[0];
        if (!uploadedFile) return;
        setLoading(true);
        try {
            const res = await ContractService.analyzePdf(uploadedFile);
            setResult(res.data);
            toast.success("PDF Analyzed Successfully!");
        } catch (err) {
            const msg = err.response?.data?.message || err.response?.data || "Failed to read PDF";
            toast.error(typeof msg === 'string' ? msg : "Failed to read PDF");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
            <Grid container spacing={3}>
                {/* Input Area */}
                <Grid item xs={12} md={6}>
                    <Paper elevation={3} sx={{ p: 3, height: '80vh', display: 'flex', flexDirection: 'column', bgcolor: '#1e293b', color: 'white' }}>
                        <Typography variant="h5" sx={{ mb: 2, color: '#60a5fa' }}>Input Contract</Typography>

                        <Button
                            variant="outlined"
                            component="label"
                            startIcon={<UploadFile />}
                            sx={{ mb: 2, color: 'white', borderColor: '#475569', '&:hover': { borderColor: '#60a5fa' } }}
                        >
                            Upload PDF
                            <input type="file" hidden accept="application/pdf" onChange={handleFileUpload} />
                        </Button>

                        <Divider sx={{ my: 1, borderColor: '#475569' }}>OR PASTE TEXT</Divider>

                        <TextField
                            multiline
                            rows={20}
                            variant="outlined"
                            placeholder="Paste contract text here..."
                            value={text}
                            onChange={(e) => setText(e.target.value)}
                            fullWidth
                            sx={{
                                bgcolor: '#0f172a',
                                borderRadius: 1,
                                '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: '#334155' } }
                            }}
                        />

                        <Button
                            variant="contained"
                            size="large"
                            onClick={handleTextAnalyze}
                            disabled={loading}
                            sx={{ mt: 2, bgcolor: '#3b82f6' }}
                        >
                            {loading ? <CircularProgress size={24} color="inherit" /> : "Analyze Risks"}
                        </Button>
                    </Paper>
                </Grid>

                {/* Results Area */}
                <Grid item xs={12} md={6}>
                    <Paper elevation={3} sx={{ p: 3, height: '80vh', overflowY: 'auto', bgcolor: '#f8fafc' }}>
                        {!result ? (
                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'gray' }}>
                                <Typography>Upload or paste a contract to see risks here.</Typography>
                            </Box>
                        ) : (
                            <>
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                                    <Typography variant="h4" fontWeight="bold" color="text.primary">Risk Report</Typography>
                                    <Box sx={{
                                        p: 2, borderRadius: '50%',
                                        bgcolor: result.overallRiskScore > 70 ? '#fee2e2' : '#dcfce7',
                                        color: result.overallRiskScore > 70 ? '#dc2626' : '#16a34a',
                                        fontWeight: 'bold', border: '2px solid currentColor'
                                    }}>
                                        {result.overallRiskScore}/100
                                    </Box>
                                </Box>

                                <Typography variant="h6" sx={{ mb: 1, color: 'black' }}>Summary</Typography>
                                <Typography paragraph sx={{ color: '#475569' }}>{result.summary}</Typography>
                                <Divider sx={{ my: 3 }} />

                                <Typography variant="h6" sx={{ mb: 2, color: '#dc2626' }}>Red Flags & Clauses</Typography>
                                {result.clauses && result.clauses.map((clause, index) => (
                                    <Alert
                                        severity={clause.riskScore > 50 ? "error" : "warning"}
                                        key={index}
                                        sx={{ mb: 2 }}
                                    >
                                        <Typography variant="subtitle2" fontWeight="bold">
                                            {clause.category || "Risky Clause"} (Risk: {clause.riskScore})
                                        </Typography>
                                        <Typography variant="body2" sx={{ mt: 1, fontStyle: 'italic', color: '#334155' }}>
                                            "{clause.originalText.substring(0, 150)}..."
                                        </Typography>
                                        <Divider sx={{ my: 1 }} />
                                        <Typography variant="body2">
                                            <strong>Analysis:</strong> {clause.explanation}
                                        </Typography>
                                    </Alert>
                                ))}
                            </>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
};

export default Analysis;