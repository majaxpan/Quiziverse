// $(document).ready(function() {
//     let score = 0;
//     let lives = 3;
//     let timer;
//     let questions = [];
//     let currentQuestionIndex = 0;
//     let timeLeft = 60;
//
//     // Load questions from server
//     function loadQuestions(category) {
//         $.getJSON(`/api/quiz/questions?category=${category}`, function(data) {
//             questions = data;
//             $('#rules-dialog').show();
//         });
//     }
//
//     // Start quiz on button click
//     $('#start-quiz').click(function() {
//         $('#rules-dialog').hide();
//         $('#quiz-content').show();
//         startTimer();
//         showQuestion();
//     });
//
//     // Show next question
//     function showQuestion() {
//         if (currentQuestionIndex >= questions.length) {
//             endGame();
//             return;
//         }
//
//         let question = questions[currentQuestionIndex];
//         $('#question-container').html(`
//             <h3>${question.questionText}</h3>
//             ${question.answers.map(answer => `
//                 <button class="answer-btn" data-correct="${answer.correct}">${answer.text}</button>
//             `).join('')}
//         `);
//
//         // Bind answer button clicks
//         $('.answer-btn').click(function() {
//             let isCorrect = $(this).data('correct');
//             if (isCorrect) {
//                 score++;
//             } else {
//                 lives--;
//             }
//             $('#score').text(`Score: ${score}`);
//             $('#lives').text(`Lives: ${lives}`);
//             currentQuestionIndex++;
//             if (lives <= 0 || currentQuestionIndex >= questions.length) {
//                 endGame();
//             } else {
//                 showQuestion();
//             }
//         });
//     }
//
//     // Start the timer
//     function startTimer() {
//         $('#time').text(timeLeft);
//         timer = setInterval(function() {
//             timeLeft--;
//             $('#time').text(timeLeft);
//             if (timeLeft <= 0) {
//                 clearInterval(timer);
//                 endGame();
//             }
//         }, 1000);
//     }
//
//     // End the game
//     function endGame() {
//         $('#quiz-content').hide();
//         $('#end-dialog').show();
//         if (lives <= 0) {
//             $('#end-message').text(`Game Over! Your score: ${score}`);
//         } else {
//             $('#end-message').text(`Time's Up! Your score: ${score}`);
//         }
//     }
//
//     // Restart quiz
//     $('#restart-quiz').click(function() {
//         location.reload();
//     });
//
//     // Load quiz when the page is ready
//     let category = [[${category}]]; // Ensure this is dynamically set
//     loadQuestions(category);
// });
