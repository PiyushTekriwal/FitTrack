import api from './api';

export const generateRecommendation = (type = 'COMPREHENSIVE') => api.post('/recommendations/generate', { type });
export const getLatestRecommendation = () => api.get('/recommendations/latest');
export const getAllRecommendations = () => api.get('/recommendations');
