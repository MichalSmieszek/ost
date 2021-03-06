// ------------------------------------
// Constants
// ------------------------------------

import { origin } from './../../../config/Api'
import axios from 'axios'
import { getHeaders, errorHandle, freeQueue } from '../../../store/addons'
import { toastr } from 'react-redux-toastr'

const toastrOptions = {
  timeOut: 3000
}

export const GET_VIEW_TRIALS = 'GET_VIEW_TRIALS'
export const GET_TRIAL_SESSION = 'GET_TRIAL_SESSION'
export const GET_TRIALS = 'GET_TRIALS'
export const CLEAR_TRIALS = 'CLEAR_TRIALS'
export const REMOVE_ANSWER = 'REMOVE_ANSWER'
export const EDIT_COMMENT = 'EDIT_COMMENT'
// ------------------------------------
// Actions
// ------------------------------------

export const getViewTrialsAction = (data = null) => {
  return {
    type: GET_VIEW_TRIALS,
    data: data
  }
}

export const getTrialSessionAction = (data = null) => {
  return {
    type: GET_TRIAL_SESSION,
    data: data
  }
}

export const getTrialsAction = (data = null) => {
  return {
    type: GET_TRIALS,
    data: data
  }
}

export const clearTrialsAction = (data = null) => {
  return {
    type: CLEAR_TRIALS,
    data: []
  }
}

export const removeAnswerAction = (data = null) => {
  return {
    type: REMOVE_ANSWER,
    data: data
  }
}

export const editCommentAction = (data = null) => {
  return {
    type: EDIT_COMMENT,
    data: data
  }
}

export const actions = {
  getViewTrials,
  getTrials,
  removeAnswer,
  editComment
}

export const getViewTrials = (trialsessionId) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.get(`${origin}/api/answers-events?trialsession_id=${trialsessionId}`, getHeaders())
       .then((response) => {
         freeQueue()
         window.indexedDB.open('driver', 1).onsuccess = (event) => {
           let item
           let answers = event.target.result.transaction(['answer'], 'readwrite').objectStore('answer')
           let delAns = answers.clear()
           delAns.onsuccess = (x) => {
             let events = event.target.result.transaction(['event'], 'readwrite').objectStore('event')
             let delEvn = events.clear()
             delEvn.onsuccess = (x) => {
               answers = event.target.result.transaction(['answer'], 'readwrite').objectStore('answer')
               events = event.target.result.transaction(['event'], 'readwrite').objectStore('event')
               for (let i = 0; i < response.data.length; i++) {
                 if (response.data[i] && response.data[i].type === 'ANSWER') {
                   item = answers.get(response.data[i].id)
                   item.onsuccess = (x) => {
                     if (!x.target.result) {
                       answers.add(Object.assign(response.data[i],
                  { trialsession_id: trialsessionId }))
                     }
                   }
                 } else if (response.data[i] && response.data[i].type === 'EVENT') {
                   item = events.get(response.data[i].id)
                   item.onsuccess = (x) => {
                     if (!x.target.result) {
                       events.add(Object.assign(response.data[i],
                  { trialsession_id: trialsessionId }))
                     }
                   }
                 }
               }
             }
           }
         }
         dispatch(getViewTrialsAction(response.data))
         resolve()
       })
       .catch((error) => {
         if (error.message === 'Network Error') {
           window.indexedDB.open('driver', 1).onsuccess = (event) => {
             let result = []
             let check = false
             let answers = event.target.result.transaction(['answer'],
             'readonly').objectStore('answer').index('trialsession_id')
             let events = event.target.result.transaction(['event'],
             'readonly').objectStore('event').index('trialsession_id')
             answers.getAll(trialsessionId).onsuccess = e1 => {
               result = Array.concat(result, e1.target.result)
               if (!check) { check = true } else {
                 let d = result.sort(function (a, b) {
                  // Turn your strings into dates, and then subtract them
                  // to get a value that is either negative, positive, or zero.
                   return new Date(b.time) - new Date(a.time)
                 })
                 dispatch(getViewTrialsAction(d))
               }
             }
             events.getAll(trialsessionId).onsuccess = e2 => {
               result = Array.concat(result, e2.target.result)
               if (!check) { check = true } else {
                 let d = result.sort(function (a, b) {
                  // Turn your strings into dates, and then subtract them
                  // to get a value that is either negative, positive, or zero.
                   return new Date(b.time) - new Date(a.time)
                 })
                 dispatch(getViewTrialsAction(d))
               }
             }
           }
         }
         errorHandle(error)
         resolve()
       })
    })
  }
}

export const getTrialSession = (trialsessionId) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.get(`${origin}/api/trialsessions/${trialsessionId}`, getHeaders())
       .then((response) => {
         freeQueue()
         window.indexedDB.open('driver', 1).onsuccess = (event) => {
           let store = event.target.result.transaction(['trial_session'],
            'readwrite').objectStore('trial_session').get(response.data.id).onsuccess = (x) => {
              if (!x.target.result) { store.add(response.data) }
            }
         }
         dispatch(getTrialSessionAction(response.data))
         resolve()
       })
       .catch((error) => {
         if (error.message === 'Network Error') {
           window.indexedDB.open('driver', 1).onsuccess = (event) => {
             event.target.result.transaction(['trial_session'],
            'readonly').objectStore('trial_session').get(Number(trialsessionId)).onsuccess = (e) => {
              dispatch(getTrialSessionAction(e.target.result))
            }
           }
         }
         errorHandle(error)
         resolve()
       })
    })
  }
}

export const getTrials = () => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.get(`${origin}/api/trialsessions/active`, getHeaders())
       .then((response) => {
         freeQueue()
         window.indexedDB.open('driver', 1).onsuccess = (event) => {
           let store = event.target.result.transaction(['trial_session'], 'readwrite').objectStore('trial_session')
           for (let i = 0; i < response.data.data.length; i++) {
             store.get(response.data.data[i].id).onsuccess = (x) => {
               if (!x.result) { store.add(response.data.data[i]) }
             }
           }
         }
         dispatch(getTrialsAction(response.data))
         resolve()
       })
       .catch((error) => {
         if (error.message === 'Network Error') {
           window.indexedDB.open('driver', 1).onsuccess = (event) => {
             event.target.result.transaction(['trial_session'],
            'readonly').objectStore('trial_session').index('status').get('ACTIVE').onsuccess = (e) => {
              dispatch(getTrialsAction({ total: e.target.result.length, data: e.target.result }))
            }
           }
         }
         errorHandle(error)
         resolve()
       })
    })
  }
}

export const clearTrialList = () => {
  return (dispatch) => {
    return new Promise((resolve) => {
      localStorage.removeItem('listOfTrials')
      dispatch(clearTrialsAction([]))
      resolve()
    })
  }
}

export const removeAnswer = (id, comment) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.delete(`${origin}/api/answers/${id}/remove?comment=${comment}`, getHeaders())
        .then(() => {
          toastr.success('Remove answer', 'Action removing answer or event has been successful!', toastrOptions)
          dispatch(removeAnswerAction())
          resolve()
        })
        .catch((error) => {
          if (error.message === 'Network Error') {
            toastr.warning('Offline mode', 'Message will be send later', toastrOptions)
            if (localStorage.getItem('online')) { localStorage.removeItem('online') }
            window.indexedDB.open('driver', 1).onsuccess = event => {
              event.target.result.transaction(['sendQueue'], 'readwrite').objectStore('sendQueue').add({
                type: 'delete',
                address: `${origin}/api/answers/${id}/remove?comment=${comment}`,
                data: {}
              })
            }
          }
          errorHandle(error)
          resolve(error)
        })
    })
  }
}

export const editComment = (id, comment) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      const data = new FormData()
      data.append('comment', comment)
      axios.post(`${origin}/api/questions-answers/${id}/comment`, data, getHeaders())
          .then((response) => {
            // #TODO PWA
            dispatch(editCommentAction(response.data))
            toastr.success('Comment', 'Changed was save!', toastrOptions)
            resolve()
          })
          .catch((error) => {
            if (error.message === 'Network Error') {
              toastr.warning('Offline mode', 'Message will be send later', toastrOptions)
              if (localStorage.getItem('online')) { localStorage.removeItem('online') }
              window.indexedDB.open('driver', 1).onsuccess = event => {
                event.target.result.transaction(['sendQueue'], 'readwrite').objectStore('sendQueue').add({
                  type: 'post',
                  address: `${origin}/api/questions-answers/${id}/comment`,
                  data: data
                })
              }
            } else {
              toastr.error('Comment', 'Error!', toastrOptions)
            }
            errorHandle(error)
            resolve()
          })
    })
  }
}
// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_VIEW_TRIALS]: (state, action) => {
    return {
      ...state,
      viewTrials: action.data
    }
  },
  [GET_TRIAL_SESSION]: (state, action) => {
    return {
      ...state,
      trialSession: action.data
    }
  },
  [GET_TRIALS]: (state, action) => {
    return {
      ...state,
      listOfTrials: action.data
    }
  },
  [CLEAR_TRIALS]: (state, action) => {
    return {
      ...state,
      listOfTrials: action.data
    }
  },
  [EDIT_COMMENT]: (state, action) => {
    return {
      ...state,
      eidtedComment: action.data
    }
  }
}
// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  viewTrials: [],
  trialSession: {},
  listOfTrials: {},
  eidtedComment: {}
}

export default function viewTrialsReducer (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]

  return handler ? handler(state, action) : state
}
