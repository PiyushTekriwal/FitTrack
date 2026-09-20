import api from './api';

export const createActivity = (data) => api.post('/activities', data);
export const getActivities = (page = 0, size = 10) => api.get(`/activities?page=${page}&size=${size}`);
export const getRecentActivities = (days = 7) => api.get(`/activities/recent?days=${days}`);
export const getActivityStats = () => api.get('/activities/stats');
export const getActivityHistory = (page = 0, size = 20) => api.get(`/activities/history?page=${page}&size=${size}`);
export const deleteActivity = (id) => api.delete(`/activities/${id}`);
