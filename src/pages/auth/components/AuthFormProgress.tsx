// src/components/auth/AuthFormProgress.tsx
import React from "react";
import { Progress } from "@/components/ui/progress";

interface AuthFormProgressProps {
  value: number;
}

export const AuthFormProgress: React.FC<AuthFormProgressProps> = ({ value }) => {
  return (
    <div className="mt-4">
      <Progress value={value} className="h-2" />
    </div>
  );
};