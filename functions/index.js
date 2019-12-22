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
const profileImage =snap.val().profileImage
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
					    profileImage:profileImage
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

// await snap.ref.parent.parent.child("allCount").child(context.params.messageId).child("commentsCount").set(0);

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
		profileUrl:post.profileImage,
		message_key: newMessage.message_key,
		imageUrl:newMessage.imageUrl
     }
 };

	return admin.messaging().sendToTopic(groupName.replace(/\s+/g, ''),payload);
});

});

//SEND COMMENT TO GROUP
exports.sendNotifCommentToGroup = functions.database.ref('/Rooms/{roomId}/{roomName}/UserComments/{parentMessageId}/{commentId}')
.onCreate(async (snap, context) => {
const groupName = context.params.roomName;
const newMessage= snap.val();

await snap.ref.parent.parent.parent.child("allCount").child(newMessage.message_key).child("commentsCount").transaction(count =>{
	return count+1
})

return snap.ref.parent.parent.parent.child("allCount").child(newMessage.message_key)
.once("value").then(snapp => {
  const counts = snapp.val().commentsCount.toString();

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
		imageUrl:"*hak*none0#",
		message_key: newMessage.message_key,
		totalComments:counts
     }
 };
	return admin.messaging().sendToTopic(groupName.replace(/\s+/g, ''),payload);
});
});


exports.sendOneToOneChat = functions.database.ref('/Users/UserChats/{messageId}')
.onCreate((snap, context) => {
var newMessage = snap.val();
return snap.ref.parent.parent.child("UserProfile").child(newMessage.receiverUID)
.once("value").then(snapp => {
  const post = snapp.val();
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
	    profileUrl:newMessage.profileUrl,
	    message_key: newMessage.message_key

	  },
	  token: post.userToken
	};
	return admin.messaging().send(messagePayload);
	    });
});


exports.sendChatSeenToSender = functions.database.ref('/Users/UserChats/{messageId}')
.onUpdate((change, context) => {
var newMessage = change.after.val();

return change.after.ref.parent.parent.child("UserProfile")
.child(newMessage.senderUID).once("value").then(snapp => {
  const post = snapp.val();
  // do stuff with post here
	var messagePayload = {
	  data: {
	    message: "delivery_status",
	    delivery_status:newMessage.delivery_status,
	    message_key: newMessage.message_key

	  },
	  token: post.userToken
	};
	return admin.messaging().send(messagePayload);
	    });
});
