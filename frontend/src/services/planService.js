import api from './api';

export const createPlan = (data) => api.post('/plans', data);
export const getPlans = () => api.get('/plans');
export const getCurrentPlan = () => api.get('/plans/current');
export const getPlanById = (id) => api.get(`/plans/${id}`);
export const completePlanItem = (planId, itemId) => api.patch(`/plans/${planId}/items/${itemId}/complete`);
export const archivePlan = (planId) => api.patch(`/plans/${planId}/archive`);
