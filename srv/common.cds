@endpoints: [{
  path    : 'Common',
  protocol: 'odata-v4'
}]

service Common {

    action sendEmail(emailJson : String) returns String;//Send Email

}