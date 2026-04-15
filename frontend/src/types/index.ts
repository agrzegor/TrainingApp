export type UserType = 'TRAINER' | 'CUSTOMER'

export interface LoginResponse {
  token: string
  expiresIn: number
}

export interface UserDto {
  firstName: string
  lastName: string
  email: string
  phone: string
  userType: UserType
  password?: string
}

export interface CustomerDto {
  id: number
  firstName: string
  lastName: string
  phone: string
  trainerId: number | null
}

export interface TrainerDto {
  identifier: string
  firstName: string
  lastName: string
  phone: string
}

export interface TrainingSessionDto {
  id: number
  trainerId: number
  customerId: number
  customerFirstName: string
  customerLastName: string
  createdAt: string
  startDate: string
  duration: number
}

export interface ExerciseDto {
  id: number
  name: string
  externalExerciseId: string
  overview: string | null
  instruction: string[] | null
  exerciseTip: string[] | null
  videoUrl: string | null
}

export interface SessionExerciseDto {
  id: number
  trainingSessionId: number
  exerciseName: string
  exerciseId: number
  reps: number
  series: number
  weight: number
}

export interface CreateTrainingSessionRequest {
  customerId: number
  startDate: string
  duration: number
}

export interface UpdateTrainingSessionRequest {
  startDate?: string
  duration?: number
}

export interface CreateSessionExercisesRequest {
  exerciseId: number
  reps: number
  series: number
  weight: number
}

export interface ErrorResponse {
  status: number
  message: string
  timestamp: number
}
