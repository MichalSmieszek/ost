
const React = require('react-native');

const { StyleSheet, Dimensions } = React;

const deviceWidth = Dimensions.get('window').width;

export default {
  titleObservationContainer: {
    flex: 1,
    alignSelf: 'stretch',
    paddingLeft: 20,
    paddingRight: 20,
    paddingBottom: 10,
    paddingTop: 15,
  },
  titleObservation: {
    fontSize: 20,
    alignSelf: 'flex-start',
    fontWeight: '600',
  },
  desObservationContainer: {
    flex: 1,
    alignSelf: 'stretch',
    paddingLeft: 30,
    paddingRight: 30,
  },
  desObservation: {
    fontSize: 16,
    alignSelf: 'flex-start',
  },
  inputStyle: {
    color: '#000',
    paddingRight: 5,
    paddingLeft: 5,
    fontSize: 18,
    lineHeight: 23,
    flex: 2,
  },
  dateText: {
    color: '#000',
    paddingRight: 5,
    paddingLeft: 5,
    paddingTop: 5,
    paddingBottom: 5,
    fontSize: 18,
    lineHeight: 23,
    alignSelf: 'center',
  },
  cardStyle: {
    borderWidth: 1,
    borderRadius: 2,
    borderColor: '#ddd',
    borderBottomWidth: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 1,
    marginTop: 10,
  },
  locationContainer: {
    width: (deviceWidth / 2) - 44,
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: 20,
  },
  locationLabel: {
    flex: 2,
  },
  locationInput: {
    flex: 3,
    color: '#000',
    borderColor: '#000',
  },
  buttonLocation: {
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
    flexDirection: 'row',
    borderRadius: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 3,
    backgroundColor: '#f4f4f4',
    borderColor: '#ddd',
    borderTopWidth: 1,
    flex: 3,
    height: 30,
  },
  buttonLocContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'flex-end',
  },
  addPhoto: {
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
    borderRadius: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 3,
    backgroundColor: '#f4f4f4',
    alignSelf: 'stretch',
    padding: 5,
    borderColor: '#ddd',
    borderTopWidth: 1,
  },
};
