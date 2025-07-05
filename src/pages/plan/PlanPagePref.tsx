'use client'

import React, { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { Cigarette, DollarSign, Package, ArrowRight, AlertCircle, CheckCircle2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card } from '@/components/ui/card'

interface FormData {
  cigarettesPerDay: string
  cigarettesPerPack: string
  costPerPack: string
}

interface FormErrors {
  cigarettesPerDay?: string
  cigarettesPerPack?: string
  costPerPack?: string
}

interface InputFieldProps {
  id: string
  label: string
  value: string
  onChange: (value: string) => void
  error?: string
  icon: React.ReactNode
  placeholder: string
  type?: string
}

const InputField: React.FC<InputFieldProps> = ({
  id,
  label,
  value,
  onChange,
  error,
  icon,
  placeholder,
  type = 'text'
}) => {
  const [isValid, setIsValid] = useState(false)

  useEffect(() => {
    setIsValid(value.length > 0 && !error)
  }, [value, error])

  return (
    <div className="space-y-2">
      <Label htmlFor={id} className="text-sm font-medium text-foreground">
        {label}
      </Label>
      <div className="relative">
        <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground">
          {icon}
        </div>
        <Input
          id={id}
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className={`pl-10 pr-10 transition-all duration-200 ${error
            ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
            : isValid
              ? 'border-green-500 focus:border-green-500 focus:ring-green-500'
              : 'border-border focus:border-primary focus:ring-primary'
            }`}
          aria-invalid={!!error}
          aria-describedby={error ? `${id}-error` : undefined}
        />
        {isValid && (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-green-500">
            <CheckCircle2 size={16} />
          </div>
        )}
        {error && (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-red-500">
            <AlertCircle size={16} />
          </div>
        )}
      </div>
      {error && (
        <motion.p
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          id={`${id}-error`}
          className="text-sm text-red-500 flex items-center gap-1"
          role="alert"
        >
          <AlertCircle size={14} />
          {error}
        </motion.p>
      )}
    </div>
  )
}

const SmokingIllustration: React.FC = () => {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.8, delay: 0.2 }}
      className="relative w-full max-w-md mx-auto"
    >
      <div className="relative bg-gradient-to-br from-blue-50 to-green-50 dark:from-blue-950/20 dark:to-green-950/20 rounded-3xl p-8 border border-border">
        <motion.div
          animate={{ rotate: [0, 5, -5, 0] }}
          transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
          className="text-center"
        >
          <div className="relative inline-block">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ duration: 0.5, delay: 0.5 }}
              className="w-24 h-24 bg-gradient-to-br from-green-400 to-green-600 rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg"
            >
              <motion.div
                animate={{ scale: [1, 1.1, 1] }}
                transition={{ duration: 2, repeat: Infinity }}
                className="text-white text-3xl"
              >
                🌱
              </motion.div>
            </motion.div>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 1, delay: 1 }}
              className="absolute -top-2 -right-2 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center text-sm"
            >
              ✨
            </motion.div>
          </div>
          <motion.h3
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.8 }}
            className="text-lg font-semibold text-foreground mb-2"
          >
            Your Journey Starts Here
          </motion.h3>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 1 }}
            className="text-sm text-muted-foreground"
          >
            Every step towards quitting is a victory
          </motion.p>
        </motion.div>
      </div>
    </motion.div>
  )
}

const PlanPagePref: React.FC = () => {
  const [formData, setFormData] = useState<FormData>({
    cigarettesPerDay: '',
    cigarettesPerPack: '',
    costPerPack: ''
  })

  const [errors, setErrors] = useState<FormErrors>({})
  const [isLoading, setIsLoading] = useState(false)
  const [isFormValid, setIsFormValid] = useState(false)

  const validateField = (name: keyof FormData, value: string): string | undefined => {
    if (!value.trim()) {
      return 'This field is required'
    }

    const numValue = parseFloat(value)

    if (isNaN(numValue) || numValue < 0) {
      return 'Please enter a valid positive number'
    }

    switch (name) {
      case 'cigarettesPerDay':
        if (numValue > 100) return 'Please enter a realistic number (max 100)'
        if (numValue === 0) return 'Please enter a number greater than 0'
        break
      case 'cigarettesPerPack':
        if (numValue > 50) return 'Please enter a realistic pack size (max 50)'
        if (numValue === 0) return 'Please enter a number greater than 0'
        break
      case 'costPerPack':
        if (numValue > 100) return 'Please enter a realistic cost (max $100)'
        if (numValue === 0) return 'Please enter a cost greater than $0'
        break
    }

    return undefined
  }

  const handleInputChange = (name: keyof FormData, value: string) => {
    setFormData(prev => ({ ...prev, [name]: value }))

    const error = validateField(name, value)
    setErrors(prev => ({ ...prev, [name]: error }))
  }

  useEffect(() => {
    const hasErrors = Object.values(errors).some(error => error)
    const hasAllValues = Object.values(formData).every(value => value.trim())
    setIsFormValid(hasAllValues && !hasErrors)
  }, [formData, errors])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const newErrors: FormErrors = {}
    Object.entries(formData).forEach(([key, value]) => {
      const error = validateField(key as keyof FormData, value)
      if (error) newErrors[key as keyof FormData] = error
    })

    setErrors(newErrors)

    if (Object.keys(newErrors).length === 0) {
      setIsLoading(true)
      await new Promise(resolve => setTimeout(resolve, 2000))
      setIsLoading(false)
      console.log('Form submitted:', formData)
    }
  }

  return (
    <div className="py-40 min-h-screen bg-background">
      <div className="container mx-auto px-4 py-8">
        <div className="grid lg:grid-cols-2 gap-12 items-center max-w-7xl mx-auto">
          {/* Left Column - Motivational Content */}
          <motion.div
            initial={{ opacity: 0, x: -50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8 }}
            className="space-y-8"
          >
            <div className="space-y-6">
              <motion.h1
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8, delay: 0.2 }}
                className="text-4xl lg:text-5xl font-bold text-foreground leading-tight"
              >
                Take Control of Your
                <span className="bg-gradient-to-r from-green-600 to-blue-600 bg-clip-text text-transparent">
                  {" "}Health Journey
                </span>
              </motion.h1>

              <motion.p
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8, delay: 0.4 }}
                className="text-xl text-muted-foreground leading-relaxed"
              >
                Start your smoke-free life today. We'll help you track your progress,
                calculate your savings, and celebrate every milestone on your journey to better health.
              </motion.p>

              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8, delay: 0.6 }}
                className="flex flex-wrap gap-4"
              >
                {[
                  { icon: '💪', text: 'Build Strength' },
                  { icon: '💰', text: 'Save Money' },
                  { icon: '🫁', text: 'Breathe Better' }
                ].map((item, index) => (
                  <div key={index} className="flex items-center gap-2 bg-muted px-4 py-2 rounded-full">
                    <span className="text-lg">{item.icon}</span>
                    <span className="text-sm font-medium text-foreground">{item.text}</span>
                  </div>
                ))}
              </motion.div>
            </div>

            <SmokingIllustration />
          </motion.div>

          {/* Right Column - Form */}
          <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8, delay: 0.3 }}
          >
            <Card className="p-8 shadow-xl border-border">
              <div className="space-y-6">
                <div className="text-center space-y-2">
                  <h2 className="text-2xl font-bold text-foreground">
                    Let's Get Started
                  </h2>
                  <p className="text-muted-foreground">
                    Tell us about your current smoking habits
                  </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6" noValidate>
                  <InputField
                    id="cigarettesPerDay"
                    label="Cigarettes per day"
                    value={formData.cigarettesPerDay}
                    onChange={(value) => handleInputChange('cigarettesPerDay', value)}
                    error={errors.cigarettesPerDay}
                    icon={<Cigarette size={16} />}
                    placeholder="e.g., 20"
                    type="number"
                  />

                  <InputField
                    id="cigarettesPerPack"
                    label="Cigarettes per pack"
                    value={formData.cigarettesPerPack}
                    onChange={(value) => handleInputChange('cigarettesPerPack', value)}
                    error={errors.cigarettesPerPack}
                    icon={<Package size={16} />}
                    placeholder="e.g., 20"
                    type="number"
                  />

                  <InputField
                    id="costPerPack"
                    label="Cost per pack ($)"
                    value={formData.costPerPack}
                    onChange={(value) => handleInputChange('costPerPack', value)}
                    error={errors.costPerPack}
                    icon={<DollarSign size={16} />}
                    placeholder="e.g., 12.50"
                    type="number"
                  />

                  <Button
                    type="submit"
                    disabled={!isFormValid || isLoading}
                    className="w-full h-12 text-base font-medium transition-all duration-200 hover:scale-[1.02] disabled:scale-100"
                  >
                    {isLoading ? (
                      <motion.div
                        animate={{ rotate: 360 }}
                        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                        className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                      />
                    ) : (
                      <>
                        Continue Your Journey
                        <ArrowRight className="ml-2" size={16} />
                      </>
                    )}
                  </Button>

                  {!isFormValid && Object.values(formData).some(value => value.trim()) && (
                    <motion.p
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="text-sm text-muted-foreground text-center"
                    >
                      Please fill in all fields correctly to continue
                    </motion.p>
                  )}
                </form>
              </div>
            </Card>
          </motion.div>
        </div>
      </div>
    </div>
  )
}

export default PlanPagePref
