// src/components/auth/AuthCard.tsx
import type React from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "@/components/ui/card"
import { EmailInput } from "./EmailInput"
import { PasswordInput } from "./PasswordInput"
import { SubmitButton } from "./SubmitButton"
import { AuthFooter } from "./AuthFooter" // Import AuthFooter

interface AuthCardProps {
  onSubmit: (e: React.FormEvent) => void
  isEmailValid: boolean | null
  isPasswordValid: boolean | null
  email: string
  setEmail: (email: string) => void
  validateEmail: (email: string) => void
  password: string
  setPassword: (password: string) => void
  validatePassword: (password: string) => void
  isSubmitting: boolean
}

export const AuthCard: React.FC<AuthCardProps> = ({
  onSubmit,
  isEmailValid,
  isPasswordValid,
  email,
  setEmail,
  validateEmail,
  password,
  setPassword,
  validatePassword,
  isSubmitting,
}) => {
  return (
    <Card className="form-card bg-white/80 dark:bg-slate-900/80">
      <CardHeader>
        <CardTitle className="text-2xl text-center">Welcome Back</CardTitle>
        <CardDescription className="text-center">Login to continue your journey</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="space-y-6">
          <EmailInput
            email={email}
            setEmail={setEmail}
            validateEmail={validateEmail}
            isEmailValid={isEmailValid}
          />
          <PasswordInput
            password={password}
            setPassword={setPassword}
            validatePassword={validatePassword}
            isPasswordValid={isPasswordValid}
          />
          <SubmitButton
            isEmailValid={isEmailValid}
            isPasswordValid={isPasswordValid}
            isSubmitting={isSubmitting}
          />
        </form>
      </CardContent>
      <CardFooter className="flex flex-col space-y-4">
        <AuthFooter />
      </CardFooter>
    </Card>
  )
}