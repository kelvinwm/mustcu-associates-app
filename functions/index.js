const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.createNewGroupAndSubscribeUsers = functions.database.ref('/Rooms/{roomId}/{roomName}/UserTokens/{userUID}')
.onWrite((change, context) => {
    const groupTokens= change.after.val();
    const groupName = context.params.roomName;

			// These registration tokens come from the client FCM SDKs.
			// var registrationTokens = [groupTokens];

			// Subscribe the devices corresponding to the registration tokens to the
			// topic.
			return admin.messaging().subscribeToTopic(groupTokens, groupName).then(function(response) {
			    // See the MessagingTopicManagementResponse reference documentation
			    // for the contents of response.
			    // console.log('Successfully subscribed to topic:', response);
			     return null;
			  }).catch(function(error) {
			    console.log('Error subscribing to topic:', error);
			     return null;
			  });

});

exports.sendUserNotification = functions.database.ref('/Rooms/{roomName}/UserChats/{messageId}')
.onWrite((change, context) => {
	const groupName = context.params.roomName;
	const newMessage= change.after.val();
	 const payload = {
	 	notification: {
            title: 'Associates',
            body: `${groupName}: ${newMessage.message}`
        },
	 	data: {
         title: `${groupName}`,
         body: `${newMessage.message}`
         }
     };
		return admin.messaging().sendToTopic(groupName,payload)
		    .then(function(response){
		         console.log('Notification sent successfully:',response);
		         return null;
		    }) 
		    .catch(function(error){
		         console.log('Notification sent failed:',error);
		         return null;
		    });
});
exports.sendOneToOneChat = functions.database.ref('/Users/UserChats/{messageId}')
.onCreate((snap, context) => {
	var newMessage = snap.val();

    return snap.ref.parent.parent.child("UserProfile").child(newMessage.receiverUID)
    .once("value").then(snap => {
      const post = snap.val();
      // do stuff with post here
		var messagePayload = {
		  data: {
		    message: newMessage.message,
		    sender: newMessage.senderName,
		    phoneNumber: newMessage.phoneNumber,
		    senderUID: newMessage.senderUID,
		    timestamp: newMessage.timestamp,
		    receiverUID:newMessage.receiverUID
		  },
		  token: post.userToken
		};
		return admin.messaging().send(messagePayload)
			  .then((response) => {
			    // Response is a message ID string.
			    console.log('Successfully sent message:', response);
			    return null;
			  })
			  .catch((error) => {
			    console.log('Error sending message:', error);
			     return null;
			  });
		    });
  });


// 	const groupName = context.params.roomName;
// 	const newMessage= change.after.val();

// 	const receiverToken = change.ref.parent.parent.child("UserProfile").child(newMessage.receiverUid).child("userToken");
// 	console.log('Here is the receiver token:',receiverToken);

				// var messagePayload = {
				//   data: {
				//     message: newMessage.message,
				//     time: '2:45'
				//   },
				//   token: receiverToken
				// };
// 				// Send a message to the device corresponding to the provided
// 				// registration token.
				// admin.messaging().send(messagePayload)
				//   .then((response) => {
				//     // Response is a message ID string.
				//     console.log('Successfully sent message:', response);
				//     return null;
				//   })
				//   .catch((error) => {
				//     console.log('Error sending message:', error);
				//      return null;
				//   });
// });