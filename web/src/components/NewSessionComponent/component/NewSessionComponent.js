import React, { Component } from 'react'
import { RaisedButton, FloatingActionButton } from 'material-ui'
import Dropzone from 'react-dropzone'
import FontIcon from 'material-ui/FontIcon'
import SelectField from 'material-ui/SelectField'
import Checkbox from 'material-ui/Checkbox'
import TextField from 'material-ui/TextField'
import './NewSessionComponent.scss'
import ContentAdd from 'material-ui/svg-icons/content/add'
import PropTypes from 'prop-types'
import MenuItem from 'material-ui/MenuItem'
import { browserHistory } from 'react-router'
import ReactTooltip from 'react-tooltip'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import { RadioButton, RadioButtonGroup } from 'material-ui/RadioButton'
import _ from 'lodash'

const statusList = [
  { id: 0, name: 'ACTIVE' },
  { id: 1, name: 'SUSPENDED' }
]

const styles = {
  radioBox: {
    marginTop: 15
  },
  radioButton: {
    marginBottom: 5
  },
  radioLabel: {
    fontWeight: 'normal',
    color: 'rgba(40, 40, 41, 0.6)'
  }
}

class NewSessionComponent extends Component {
  constructor (props) {
    super(props)
    this.state = {
      rolesList: [],
      stagesList: [],
      stageItem: '',
      status: '',
      loginPrefix: '',
      userItems: [],
      listOfemails: [],
      openModal: false,
      type: ''
    }
  }

  static propTypes = {
    getRoles: PropTypes.func,
    rolesList: PropTypes.object,
    getStages: PropTypes.func,
    stagesList: PropTypes.object,
    newSession: PropTypes.func,
    session: PropTypes.object,
    params: PropTypes.any
  }

  componentWillMount () {
    this.props.getRoles(this.props.params.id)
    this.props.getStages(this.props.params.id)
  }

  componentWillReceiveProps (nextProps) {
    if (nextProps.stagesList &&
      nextProps.stagesList !== this.props.stagesList) {
      this.setState({ stagesList: nextProps.stagesList.data })
    }
    if (nextProps.rolesList &&
      nextProps.rolesList !== this.props.rolesList) {
      this.setState({ rolesList: nextProps.rolesList.data },
        () => this.createUserItem())
    }
    if (nextProps.session && nextProps.session !== this.props.session) {
      browserHistory.push(`/trial-manager`)
    }
  }

  handleChangeDropDown (stateName, event, index, value) {
    let change = { ...this.state }
    change[stateName] = value
    this.setState(change)
  }

  createUserItem () {
    let items = [ ...this.state.userItems ]
    let lastItem = _.last(items)
    let id = 0
    if (lastItem) {
      id = lastItem.id + 1
    }
    items.push({
      id: id,
      email: 'user@driverplus.itti.com.pl',
      role: []
    })
    this.setState({
      userItems: items
    })
  }

  handleChangeCheckbox (id, roleName) {
    let change = [ ...this.state.userItems ]
    let checkIndex = _.findIndex(change, { id: id })
    let isIn = _.findIndex(change[checkIndex].role, { name: roleName })
    if (isIn !== -1) {
      change[checkIndex].role.splice(isIn, 1)
    } else {
      change[checkIndex].role.push({ name: roleName })
    }
    this.setState({ userItems: change })
  }

  handleCheckRole (object, roleName) {
    let isCheck = false
    if (object.role.length !== 0) {
      object.role.map(element => {
        if (element.name === roleName) {
          isCheck = true
        }
      })
      return isCheck
    }
  }

  handleRemoveUser (id) {
    let change = [ ...this.state.userItems ]
    let checkIndex = _.findIndex(change, { id: id })
    change.splice(checkIndex, 1)
    this.setState({ userItems: change })
  }

  back = () => {
    browserHistory.push(`/trial-manager`)
  }

  handleChangeEmail (object, e) {
    let change = [ ...this.state.userItems ]
    let checkIndex = _.findIndex(change, { id: object.id })
    change[checkIndex].email = e.target.value
    this.setState({ userItems: change })
  }

  handleValidEmail (object) {
    let regex = new RegExp('[^@]+@[^@]+\\.[^@]+')
    let validated = regex.test(object.email)
    if (!validated) {
      return 'The email is not correct!'
    } else {
      return ''
    }
  }

  handleChangeLoginPrefix (name, e) {
    let change = { ...this.state }
    change[name] = e.target.value
    this.setState(change)
  }

  validateForm () {
    let isValid = true
    let validTab = []
    if (this.state.stageItem !== '' && this.state.status !== '' && this.state.userItems.length !== 0) {
      this.state.userItems.map(object => {
        if (object.email === '' || object.role.length === 0) {
          validTab.push(object.email)
        }
      })
      if (validTab.length === 0) {
        isValid = false
      }
    }
    return isValid
  }

  onDrop (file) {
    if (file) {
      let reader = new FileReader()
      reader.onload = () => {
        this.setState({
          listOfemails: reader.result.split(`\n`) },
          () => this.onDrop(null)
        )
      }
      reader.readAsText(file[0])
    }
    let items = [ ...this.state.userItems ]
    let lastItem = _.last(items)
    let id = 0
    this.state.listOfemails.map(value => {
      if (items[0].email === '') {
        items[0].email = value
      } else {
        lastItem = _.last(items)
        id = lastItem.id + 1
        items.push({
          id: id,
          email: value,
          role: []
        })
      }
    })
    this.setState({ userItems: items })
  }

  handleCloseModal = () => {
    this.setState({ openModal: false })
  }

  handleOpenModal = () => {
    this.setState({ openModal: true })
  }

  handleRadioButton = (e) => {
    this.setState({ type: e.target.value })
  }

  send () {
    let change = [ ...this.state.userItems ]
    change.map(object => {
      delete object.id
      let role = []
      object.role.map(item => {
        role.push(item.name)
      })
      object.role = role
    })
    let data = {
      trialId: this.props.params.id,
      initialStage: this.state.stageItem,
      prefix: this.state.loginPrefix,
      status: this.state.status,
      users: change
    }
    let type = ''
    if (this.state.type === '') {
      type = 'email'
    } else {
      type = this.state.type
    }
    this.props.newSession(data, type)
  }

  render () {
    const actions = [
      <FlatButton
        label='Cancel'
        primary
        onTouchTap={this.handleCloseModal}
      />,
      <FlatButton
        label='OK'
        primary
        keyboardFocused
        onTouchTap={() => this.send()}
      />
    ]
    return (
      <div className='main-container'>
        <div className='pages-box' style={{ height: '100%' }}>
          <div className='new-session-container'>
            <h2 style={{ display: 'inline-block' }}>New Session</h2>
            <div className={'buttons-obs'} style={{ float: 'right', display: 'inline-block' }}>
              <RaisedButton
                buttonStyle={{ width: 300 }}
                backgroundColor='#244C7B'
                labelColor='#FCB636'
                label='Back to select the Trial'
                secondary
                icon={<FontIcon className='material-icons' style={{ margin: 0 }}>
                  <i className='material-icons'>keyboard_arrow_left</i></FontIcon>}
                onClick={this.back.bind(this)} />
            </div>
            <div style={{
              display: 'flex',
              flexDirection: 'row',
              flexFlow: 'row wrap',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div className='element'>
                <h3>Initial Stage:</h3>
                <SelectField
                  value={this.state.stageItem}
                  floatingLabelText='Stage'
                  onChange={this.handleChangeDropDown.bind(this, 'stageItem')} >
                  {this.state.stagesList.length !== 0 && this.state.stagesList.map((index) => (
                    <MenuItem
                      key={index.id}
                      value={index.name}
                      style={{ color: 'grey' }}
                      primaryText={index.name} />
                ))}
                </SelectField>
              </div>
              <div className='element'>
                <h3>Status:</h3>
                <SelectField
                  value={this.state.status}
                  floatingLabelText='Change Session Status'
                  onChange={this.handleChangeDropDown.bind(this, 'status')} >
                  {statusList.map((index) => (
                    <MenuItem
                      key={index.id}
                      value={index.name}
                      style={{ color: 'grey' }}
                      primaryText={index.name} />
                ))}
                </SelectField>
              </div>
              <div className='element'>
                <h3>Login prefix:</h3>
                <TextField
                  style={{ marginTop: 20 }}
                  value={this.state.loginPrefix}
                  hintText='enter the login prefix'
                  onChange={this.handleChangeLoginPrefix.bind(this, 'loginPrefix')} />
              </div>
            </div>
            <h3 style={{ marginTop: 50, marginBottom: 28, marginRight: 10, display: 'inline-block' }}>Users:</h3>
            <Dropzone
              accept='text/plain, .txt'
              className='btn-dropzone'
              onDrop={this.onDrop.bind(this)}>
              Import E-mails
            </Dropzone>
            {this.state.userItems.length !== 0 && this.state.userItems.map((object, index) => {
              return (
                <div key={index} className='users-row'>
                  <TextField
                    style={{ minWidth: 200, maxWidth: 200 }}
                    value={object.email}
                    hintText='enter the email'
                    errorText={object.email !== '' && this.handleValidEmail(object)}
                    onChange={this.handleChangeEmail.bind(this, object)} />
                  <div style={{
                    display: 'inherit',
                    overflowX: 'auto',
                    overflowY: 'hidden',
                    width: '100%',
                    marginLeft: 15
                  }}>
                    {this.state.rolesList.length !== 0 && this.state.rolesList.map((role, index) => {
                      return (
                        <Checkbox
                          data-tip={role.name}
                          key={index}
                          label={role.name}
                          defaultChecked={this.handleCheckRole(object, role.name)}
                          labelStyle={{
                            minWidth: 100,
                            maxWidth: 200,
                            height: 30,
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }}
                          style={{ display: 'inline-block' }}
                          onCheck={this.handleChangeCheckbox.bind(this, object.id, role.name)}
                    />)
                    }
                    )}
                    <ReactTooltip place='top' type='dark' effect='solid' />
                  </div>
                  <i className='material-icons' onClick={() => this.handleRemoveUser(object.id)}>
                    clear
                  </i>
                </div>
              )
            })}
            <div style={{ float: 'right' }}>
              <FloatingActionButton
                onClick={this.createUserItem.bind(this)}
                secondary>
                <ContentAdd />
              </FloatingActionButton>
            </div>
            <RaisedButton
              label='Submit'
              onClick={this.handleOpenModal}
              style={{ display: 'table', margin: '0 auto', width: 200, marginTop: 120 }}
              disabled={this.validateForm()}
              primary />
          </div>
        </div>
        <Dialog
          title='Trial Session Created'
          actions={actions}
          modal={false}
          open={this.state.openModal}
          onRequestClose={this.handleCloseModal}>
          What you would like to do?
          <RadioButtonGroup
            style={styles.radioBox}
            name='radioButtons'
            defaultSelected={'email'}
            onChange={this.handleRadioButton}>
            <RadioButton
              value={'email'}
              label='Invite trial participants automatically'
              labelStyle={styles.radioLabel}
              style={styles.radioButton} />
            <RadioButton
              value={'file'}
              label='Generate a list of accounts with login and passwords'
              labelStyle={styles.radioLabel}
              style={styles.radioButton} />
          </RadioButtonGroup>
        </Dialog>
      </div>
    )
  }
}

export default NewSessionComponent
