import { defineStore } from 'pinia'

export const useLoadingStore = defineStore('loading', () => {
  const loadingStates = ref(new Map())
  const globalLoading = ref(false)

  const setLoading = (key, isLoading) => {
    loadingStates.value.set(key, isLoading)
    updateGlobalLoading()
  }

  const getLoading = (key) => {
    return loadingStates.value.get(key) || false
  }

  const updateGlobalLoading = () => {
    globalLoading.value = Array.from(loadingStates.value.values()).some(isLoading => isLoading)
  }

  const withLoading = async (key, asyncFunction) => {
    try {
      setLoading(key, true)
      return await asyncFunction()
    } finally {
      setLoading(key, false)
    }
  }

  const clearAllLoading = () => {
    loadingStates.value.clear()
    globalLoading.value = false
  }

  return {
    loadingStates,
    globalLoading,
    setLoading,
    getLoading,
    withLoading,
    clearAllLoading
  }
})