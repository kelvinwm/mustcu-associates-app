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
.onCreate((snap, context) => {
    const groupTokens= snap.val().userToken;
    const groupName = context.params.roomName;
			return admin.messaging().subscribeToTopic(groupTokens, groupName.replace(/\s+/g, '')).then((response)=> {
			    const payload = {
					 	data: {
				         	message: snap.val().userPhone,
				         	groupname:groupName,
				         	newGroup:"NewGroup",
						    sender: groupName,
						    phoneNumber: "000",
						    senderUID: context.params.roomId,
						    timestamp: new Date().toLocaleString(),
						    receiverUID:context.params.roomId,
						    type:"Room",
						    imageUrl:snap.val().imageUrl
				         },
		  				token: groupTokens

	    			 };

					return admin.messaging().send(payload);

			  }).catch((error)=> {
			    console.log('Error subscribing to topic:', error);
			     return null;
		    });

});

exports.sendNotificationToGroup = functions.database.ref('/Rooms/{roomId}/{roomName}/UserChats/{messageId}')
.onCreate((snap, context)  => {
	const groupName = context.params.roomName;
	const newMessage= snap.val();
return snap.ref.parent.parent.child("GroupInfo")
    .once("value").then(snapp => {
      const post = snapp.val();

	 const payload = {
	 	data: {
         	message: newMessage.message,
         	groupname:groupName,
		    sender: newMessage.senderName,
		    phoneNumber: newMessage.phoneNumber,
		    senderUID: newMessage.senderUID,
		    timestamp: newMessage.timestamp,
		    receiverUID:context.params.roomId, //should be the unique id--- senderId kwa android
		    type:newMessage.type,
		    newGroup:"Nop",
			imageUrl:post.imageUrl,
			message_key: newMessage.message_key
         }
     };
		return admin.messaging().sendToTopic(groupName.replace(/\s+/g, ''),payload);
	});
});

//SEND COMMENT TO GROUP
exports.sendNotifCommentToGroup = functions.database.ref('/Rooms/{roomId}/{roomName}/UserComments/{parentMessageId}/{commentId}')
.onCreate((snap, context) => {
	const groupName = context.params.roomName;
	const newMessage= snap.val();
	 const payload = {
	 	data: {
         	message: newMessage.message,
         	groupname:groupName,
		    sender: newMessage.senderName,
		    phoneNumber: newMessage.phoneNumber,
		    senderUID: newMessage.senderUID,
		    timestamp: newMessage.timestamp,
		    receiverUID:context.params.roomId, //should be the unique id--- senderId kwa android
		    type:newMessage.type,
		    newGroup:"Nop",
			imageUrl:"post.imageUrl",
			message_key: newMessage.message_key
         }
     };
		return admin.messaging().sendToTopic(groupName.replace(/\s+/g, ''),payload);
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
		    receiverUID:newMessage.receiverUID,
		    type:newMessage.type,
		    imageUrl:newMessage.imageUrl,
		    message_key: newMessage.message_key

		  },
		  token: post.userToken
		};
		return admin.messaging().send(messagePayload);
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