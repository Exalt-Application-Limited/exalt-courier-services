import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Provider } from 'react-redux';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { store } from './store/store';
import Layout from './components/Layout/Layout';
import Dashboard from './pages/Dashboard/Dashboard';
import BranchOverview from './pages/BranchOverview/BranchOverview';
import ResourceAllocation from './pages/ResourceAllocation/ResourceAllocation';
import PerformanceAnalytics from './pages/PerformanceAnalytics/PerformanceAnalytics';
import PolicyManagement from './pages/PolicyManagement/PolicyManagement';
import Settings from './pages/Settings/Settings';
import Login from './pages/Auth/Login';

// Create Material-UI theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#2e7d32',
    },
    secondary: {
      main: '#ff6f00',
    },
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
  },
});

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <div className="App">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="dashboard" element={<Dashboard />} />
                <Route path="branches" element={<BranchOverview />} />
                <Route path="resources" element={<ResourceAllocation />} />
                <Route path="analytics" element={<PerformanceAnalytics />} />
                <Route path="policies" element={<PolicyManagement />} />
                <Route path="settings" element={<Settings />} />
              </Route>
            </Routes>
            <ToastContainer
              position="top-right"
              autoClose={5000}
              hideProgressBar={false}
              newestOnTop={false}
              closeOnClick
              rtl={false}
              pauseOnFocusLoss
              draggable
              pauseOnHover
            />
          </div>
        </Router>
      </ThemeProvider>
    </Provider>
  );
}

export default App;