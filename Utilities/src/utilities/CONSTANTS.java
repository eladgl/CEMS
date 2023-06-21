package utilities;

/**
 * The CONSTANTS class contains various types of constants used in the
 * application.
 */
public abstract class CONSTANTS {
	// IP address constants
	public final static int IP_ADDRESS_MIN_VAL = 0;
	public final static int IP_ADDRESS_MAX_VAL = 254;
	// Port constants
	public final static int PORT_MIN_VAL = 0;
	public final static int PORT_MAX_VAL = 65535;
	// Default port constant
	public final static int DEFAULT_PORT = 5555;
	// Database field length constants
	public final static int DB_URL_MAX_LENGTH = 100;
	public final static int DB_USERNAME_MAX_LENGTH = 20;
	public final static int DB_PASSWORD_MAX_LENGTH = 20;

	// Unknown command constant
	public final static String UnknownCommand = "UnknownCommand";

	// Client response constants
    // Operations for the client
	public final static String ValidLoginAnswer = "ValidLoginAnswer";
	public final static String reciveQbankNames = "reciveQbankNames";
	public final static String receiveQuestions = "receiveQuestions";
	public final static String receiveUserLogOutResponse = "logOutresponse";
	public final static String ResponseEditQuestion = "ResponseEditQuestion";
	public final static String ResponseAddQuestion = "ResponseAddQuestion";
	public final static String ResponseExitClient = "ResponseExitClient";
	public final static String ResponseAddCreatedExam = "ResponseAddCreatedExam";
	public final static String ResponsegetQuestionsBySubjectAndCourse = "ResponsegetQuestionsBySubjectAndCourse";
	public final static String responseToGetStudentNotTakenTests = "responseToGetStudentNotTakenTests";
	public final static String responsegetSubjectAndCoursesTeacher = "responsegetSubjectAndCoursesTeacher";
	public final static String responsegetQuestionsBySubject = "responsegetQuestionsBySubject";
	public final static String responsegetQuestionsBySubjectAndByLecturerBank = "responsegetQuestionsBySubjectAndByLecturerBank";
	public final static String responseCheckIfPasswordMatchesAndTheTestFile = "responseCheckIfPasswordMatchesAndTheTestFile";
	public final static String ResponseToGetSubjectsFromDB = "ResponseToGetSubjectsFromDB";
	public final static String responseSetStudenIsTakenExamForCurrectConductTestToOne = "responseSetStudenIsTakenExamForCurrectConductTestToOne";
	public final static String responseChangePersonalBankQuestion = "responseChangePersonalBankQuestion";
	public final static String responseGetTestInformationAtConduct = "responseGetTestInformationAtConduct";
	public final static String responseGetExamsFromDB = "responseGetExamsFromDB";
	public final static String responseGetExamsBySubject = "responseGetExamsBySubject";
	public final static String responseGetPersonalExamBank = "responseGetPersonalExamBank";
	public final static String responseChangePersonalBankExams = "responseChangePersonalBankExams";
	public final static String responseForUploadingWordFile = "responseUploadingWordFile";
	public final static String resonseForCheckIfExamTimeChange = "responseForCheckExamTimeChange";
	public final static String responseApproveTimeExtension = "responseApproveTimeExtension";
	public final static String responseDenyTimeExtension = "responseDenyTimeExtension";
	public final static String responseGetTimeExtensionRequests = "responseGetTimeExtensionRequests";
	public final static String responseGetGradesByUsername = "responseGetGradesByUsername";
	public final static String responseGetGradedWordTest = "responseGetGradedWordTest";
	public final static String responseGetStudentsUsers = "responseGetStudentsUsers";
	public final static String responseAvailablePasswordForConductExam = "responseAvailablePasswordForConductExam";
	public final static String responseStartExamToConduct = "responseStartExamToConduct";
	public final static String responseGetExamsToConductBySubjectByLecturer = "reponseGetExamsToConductBySubjectByLecturer";
	public final static String responseLectuerRequestTimeExtension = "responseLectuerRequestTimeExtension";
	public final static String responseRefreshRequestTimeExtensionStatus = "refreshRequestTimeExtensionStatus";
	public final static String responseEndConductExam = "responseEndConductExam";
	public final static String responseGetStudentsDoneTestData = "responseGetStudentsDoneTestData";
	public final static String responseUpdateStudentDataInExam = "responseUpdateStudentDataInExam";
	public final static String responseWithUpdateExamDataInDB = "responseWithUpdateManualExamDataInDB";
	public final static String responseForCheckForExamLock = "responseForCheckForExamLock";
	public final static String responseWithGetConductExamAndRelevantDataForLecturer = "responseWithGetConductExamAndRelevantDataForLecturer";
	public final static String responseRegisterUser = "responseRegisterUser";
	public final static String responseStudentGradeSMS_Simulation = "responseStudentGradeSMS_Simulation";
	public final static String responseTakenExamsForStatistics = "responseTakenExamsForStatistics";
	public final static String responseStatisticsData = "responseStatisticsData";
	public final static String responseApproveAllGradesConductExamAndCreateStatistics = "responseApproveAllGradesConductExamAndCreateStatistics";
	public final static String responseStudentsTakenTests = "responseStudentsTakenTests";
	public final static String responseUpdateProfilePicture = "responseUpdateProfilePicture";
	public final static String responseGetProfilePicture = "responseGetProfilePicture";
	
	
													
	// Server operation constants
    // Operations for the server
	public final static String CheckLogin = "CheckLogin"; // ask from server to check in DB if user name and password are in DB
	public final static String fetchBankQNamesBySubject = "fetchBankQNamesBySubject"; // ask from server to bring form DB all bankQnames that belongs to given Subjects
	public final static String getQuestionsFromDB = "getQuestionsFromDB";
	public final static String logOutUser = "logOutUser";
	public final static String EditQuestion = "EditQuestion";
	public final static String AddQuestion = "AddQuestion";
	public final static String ExitClient = "ExitClient";
	public final static String AddCreatedExam = "AddCreatedExam";
	public final static String getQuestionsBySubjectAndCourse = "getQuestionsBySubjectAndCourse";
	public final static String getConductExam = "getConductExam";
	public final static String getStudentNotTakenTests = "getStudentNotTakenTests";
	public final static String downloadTest = "downloadTest";
	public final static String getSubjectAndCoursesTeacher = "getSubjectAndCoursesTeacher";
	public final static String getQuestionsBySubject = "getQuestionsBySubject";
	public final static String getQuestionsBySubjectAndByLecturerBank = "getQuestionsBySubjectAndByLecturerBank";
	public final static String checkIfPasswordMatchesAndTheTestFile = "checkIfPasswordMatchesAndTheTestFile";
	public final static String setStudenIsTakenExamForCurrectConductTestToOne = "setStudenIsTakenExamForCurrectConductTestToOne";
	public final static String GetSubjectsFromDB = "GetSubjectsFromDB";
	public final static String getTestInformationAtConduct = "getTestInformationAtConduct";
	public final static String UploadStudentExamWordFileSolution = "uploadWordFileSolution";
	public final static String CheckForExamTimeChange = "checkForExamTimeChange";
	public final static String approveTimeExtension = "approveTimeExtension";
	public final static String ChangePersonalBankQuestion = "ChangePersonalBankQuestion";
	public final static String getExamsFromDB = "getExamsFromDB";
	public final static String getExamsBySubject = "getExamsBySubject";
	public final static String getPersonalExamBank = "getPersonalExamBank";
	public final static String ChangePersonalBankExams = "ChangePersonalBankExams";
	public final static String getGradesByUsername = "getGradesByUsername";
	public final static String getGradedWordTest = "getGradedWordTest";
	public final static String getStudentsUsers = "getStudentsUsers";
	public final static String availablePasswordForConductExam = "availablePasswordForConductExam";
	public final static String startExamToConduct = "startExamToConduct";
	public final static String getExamsToConductBySubjectByLecturer = "getExamsToConductBySubjectByLecturer";
	public final static String lectuerRequestTimeExtension = "lectuerRequestTimeExtension";
	public final static String refreshRequestTimeExtensionStatus = "refreshRequestTimeExtensionStatus";
	public final static String endConductExam = "endConductExam";
	public final static String getStudentsDoneTestData = "getStudentsDoneTestData";
	public final static String updateStudentDataInExam = "updateStudentDataInExam";
	public final static String InsertStudentExamDataIntoDB = "InsertExamDataIntoDB";
	public final static String CheckForExamLock = "CheckForExamLock";
	public final static String denyTimeExtension = "denyTimeExtension";
	public final static String getTimeExtensionRequests = "getTimeExtensionRequests";
	public final static String getStudentGradeSMS_Simulation = "getStudentGradeSMS_Simulation";
	public final static String getCourseNameAndRelevantDataExamsForLecturer = "getCourseNameAndRelevantDataExamsForLecturer";
	public final static String getTakenExamsForStatistics = "getTakenExamsForStatistics";
	public final static String getStatisticsData = "getStatisticsData";
	public final static String approveAllGradesConductExamAndCreateStatistics = "approveAllGradesConductExamAndCreateStatistics";
	public final static String registerUser = "registerUser";
	public final static String getStudentsTakenTests = "getStudentsTakenTests";
	public final static String updateProfilePicture = "updateProfilePicture";
	public final static String getProfilePicture = "getProfilePicture";

	
	// for conduct_exam


	// Conduct exam constants
    // For conduct_exam

	public final static String requested = "requested";
	public final static String not_requested = "not_requested";
    // RequestTimeExtension constants
    // For RequestTimeExtension
	public final static String Pending = "Pending";
	public final static String Approved = "Approved";
	public final static String Denied = "Denied";
	public final static String None = "None";

	// User roles constants
	public final static String studentRole = "student";
	public final static String headOfDepartmentRole = "headDepartment";
	public final static String teacherRole = "lecturer";
}
