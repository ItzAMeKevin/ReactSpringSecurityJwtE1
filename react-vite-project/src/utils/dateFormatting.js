export const formatDateFrench = (dateString) => {
    if (!dateString) return '';
    
    try {
        const [year, month, day] = dateString.split('-');
        const months = [
            'janvier', 'février', 'mars', 'avril', 'mai', 'juin', 
            'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'
        ];
        const weekdays = ['dimanche', 'lundi', 'mardi', 'mercredi', 'jeudi', 'vendredi', 'samedi'];
        
        let d = parseInt(day);
        let m = parseInt(month);
        let y = parseInt(year);
        
        if (m < 3) {
            m += 12;
            y -= 1;
        }
        
        const K = y % 100;
        const J = Math.floor(y / 100);
        const h = (d + Math.floor((13 * (m + 1)) / 5) + K + Math.floor(K / 4) + Math.floor(J / 4) - 2 * J) % 7;
        const weekday = weekdays[(h + 6) % 7];
        
        return `${weekday} ${day} ${months[month - 1]} ${year}`;
    } catch {
        return dateString;
    }
};

export const isValidDateString = (dateString) => {
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(dateString)) return false;
    
    try {
        const date = new Date(dateString + 'T00:00:00');
        return date instanceof Date && !isNaN(date);
    } catch {
        return false;
    }
};

export const getTodayDateString = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};
