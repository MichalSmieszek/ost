// ------------------------------------
// Constants
// ------------------------------------
export let origin = window.location.hostname
if (origin === 'localhost' || origin === 'dev.itti.com.pl') {
  origin = 'dev.itti.com.pl:8009'
} else {
  origin = window.location.host
}
import axios from 'axios'
import { getHeaders, getHeadersASCI, getHeadersReferences, errorHandle } from '../../../store/addons'
import fileDownload from 'react-file-download'
import { toastr } from 'react-redux-toastr'

const toastrOptions = {
  timeOut: 3000
}

export const GET_SCHEMA = 'GET_SCHEMA'
export const SEND_OBSERVATION = 'SEND_OBSERVATION'
export const DOWNLOAD_FILE = 'DOWNLOAD_FILE'
export const GET_TRIAL_TIME = 'GET_TRIAL_TIME'

// ------------------------------------
// Actions
// ------------------------------------

export const getSchemaAction = (data = null) => {
  return {
    type: GET_SCHEMA,
    data: data
  }
}

export const sendObservationAction = (data = null) => {
  return {
    type: SEND_OBSERVATION,
    data: data
  }
}

export const downloadFileAction = (data = null) => {
  return {
    type: DOWNLOAD_FILE,
    data: data
  }
}

export const getTrialTimeAction = (data = null) => {
  return {
    type: GET_TRIAL_TIME,
    data: data
  }
}

export const actions = {
  getSchema,
  sendObservation,
  downloadFile,
  getTrialTime
}

export const getSchema = (idObs, idSession) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      /* eslint-disable */
      axios.get(`http://${origin}/api/observationtypes/form?observationtype_id=${idObs}&trialsession_id=${idSession}`, getHeaders())
      /* eslint-enable */
          .then((response) => {
            dispatch(getSchemaAction(response.data))
            resolve()
          })
          .catch((error) => {
            errorHandle(error)
            resolve()
          })
    })
  }
}

export const sendObservation = (formData) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      const data = new FormData()
      let tempData = {}
      for (let key in formData) {
        if (key !== 'attachments') {
          tempData[key] = formData[key]
        }
      }
      let json = JSON.stringify(tempData)
      let blob = new Blob([json], { type: 'application/json' })
      for (var i = 0; i < formData.attachments.length; i++) {
        // add each file to the form data and iteratively name them
        data.append('attachments', formData.attachments[i])
      }

     // data.append('attachments', formData.attachments)
      data.append('data', blob)
      axios.post(`http://${origin}/api/answers`, data, getHeadersReferences())
          .then((response) => {
            dispatch(sendObservationAction(response.data))
            toastr.success('Observation form', 'Observation was send!', toastrOptions)
            resolve()
          })
          .catch((error) => {
            errorHandle(error)
            toastr.error('Observation form', 'Error! Please, check all fields in form.', toastrOptions)
            resolve()
          })
    })
  }
}

export const downloadFile = (id, name) => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.get(`http://${origin}/api/attachments/${id}`, getHeadersASCI())
     .then((response) => {
       fileDownload(response.data, name)
       resolve()
     })
     .catch((error) => {
       errorHandle(error)
       resolve()
     })
    })
  }
}

export const getTrialTime = () => {
  return (dispatch) => {
    return new Promise((resolve) => {
      axios.get(`http://${origin}/api/trial-time`, getHeaders())
      .then((response) => {
        dispatch(getTrialTimeAction(response.data))
        resolve()
      })
      .catch((error) => {
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
  [GET_SCHEMA]: (state, action) => {
    return {
      ...state,
      observationForm: action.data
    }
  },
  [SEND_OBSERVATION]: (state, action) => {
    return {
      ...state,
      observation: action.data
    }
  },
  [GET_TRIAL_TIME]: (state, action) => {
    return {
      ...state,
      trialTime: action.data
    }
  }
}
// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  observationForm: {},
  observation: {},
  trialTime: null
}

export default function newobservationReducer (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]

  return handler ? handler(state, action) : state
}
