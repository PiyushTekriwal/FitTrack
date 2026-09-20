import api from './api';

export const getUserProfile = () => api.get('/users/me');
export const updateProfile = (data) => api.put('/users/me', data);
export const getFitnessProfile = () => api.get('/users/me/fitness-profile');
export const getUserFitnessProfile = () => api.get('/users/me/fitness-profile');
export const updateFitnessProfile = (data) => api.put('/users/me/fitness-profile', data);

