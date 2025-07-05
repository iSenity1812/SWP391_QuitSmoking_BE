import axios from '../config/axiosConfig';

export interface Qualification {
  qualificationName: string;
  issuingOrganization?: string;
  qualificationURL?: string;
  isApproved: boolean;
  requestUpdateDate: string;
  approveBy?: string;
}

export const getQualifications = (coachId: string): Promise<Qualification[]> =>
  axios.get(`/coaches/${coachId}/qualifications`).then((res: { data: Qualification[] }) => res.data);

export const createQualification = (coachId: string, data: Partial<Qualification>): Promise<Qualification> =>
  axios.post(`/coaches/${coachId}/qualifications`, data).then((res: { data: Qualification }) => res.data);

export const updateQualification = (coachId: string, qualificationName: string, data: Partial<Qualification>): Promise<Qualification> =>
  axios.put(`/coaches/${coachId}/qualifications/${qualificationName}`, data).then((res: { data: Qualification }) => res.data);

export const deleteQualification = (coachId: string, qualificationName: string): Promise<void> =>
  axios.delete(`/coaches/${coachId}/qualifications/${qualificationName}`);

export const approveQualification = (coachId: string, qualificationName: string, adminUserId: string): Promise<Qualification> =>
  axios.put(`/coaches/${coachId}/qualifications/${qualificationName}/approve?adminUserId=${adminUserId}`).then((res: { data: Qualification }) => res.data); 