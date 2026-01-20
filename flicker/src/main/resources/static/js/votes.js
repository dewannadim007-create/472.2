// Vote System with Visual Feedback
document.addEventListener('DOMContentLoaded', function () {
    const voteBtns = document.querySelectorAll('.vote-btn');

    voteBtns.forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();

            const postId = this.dataset.postId;
            const voteType = this.classList.contains('upvote') ? 'up' : 'down';
            const voteContainer = this.closest('.post-actions');
            const upvoteBtn = voteContainer.querySelector('.upvote');
            const downvoteBtn = voteContainer.querySelector('.downvote');
            const voteCount = voteContainer.querySelector('.vote-count');

            // Toggle vote
            if (this.classList.contains('voted')) {
                // Remove vote
                this.classList.remove('voted');
                updateVoteCount(voteCount, voteType === 'up' ? -1 : 1);
            } else {
                // Add vote and remove opposite
                if (voteType === 'up') {
                    if (downvoteBtn.classList.contains('voted')) {
                        downvoteBtn.classList.remove('voted');
                        updateVoteCount(voteCount, 2); // Remove downvote and add upvote
                    } else {
                        updateVoteCount(voteCount, 1);
                    }
                    upvoteBtn.classList.add('voted');
                } else {
                    if (upvoteBtn.classList.contains('voted')) {
                        upvoteBtn.classList.remove('voted');
                        updateVoteCount(voteCount, -2); // Remove upvote and add downvote
                    } else {
                        updateVoteCount(voteCount, -1);
                    }
                    downvoteBtn.classList.add('voted');
                }
            }

            // Here you would make an AJAX call to save the vote
            // saveVote(postId, voteType);
        });
    });
});

function updateVoteCount(element, change) {
    const current = parseInt(element.textContent) || 0;
    element.textContent = current + change;

    // Add pulse animation
    element.style.transform = 'scale(1.2)';
    setTimeout(() => {
        element.style.transform = 'scale(1)';
    }, 200);
}

// Optional: Save vote to backend
function saveVote(postId, voteType) {
    // Implement your AJAX call here
    console.log(`Saving ${voteType}vote for post ${postId}`);
}
