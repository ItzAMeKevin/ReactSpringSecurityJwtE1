import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { btnAccept, btnOutline, btnPrimary, inputField, textError } from '../../../styles/classes.js';
import { getTodayDateString } from '../../../utils/dateFormatting.js';

const DATE_FORMAT_OPTIONS = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };

const InterviewSlotsForm = ({ slots, onAddSlot, onRemoveSlot, onModifySlot, interviewType, videoLink, address, notes, onFieldChange, submissionAttempted }) => {
    const { t, i18n } = useTranslation();
    const [newSlot, setNewSlot] = useState({
        date: '',
        startTime: '',
        endTime: ''
    });
    const [editingSlotId, setEditingSlotId] = useState(null);
    const [editSlot, setEditSlot] = useState({ date: '', startTime: '', endTime: '' });
    const [errors, setErrors] = useState({});

    const today = getTodayDateString();

    const validateSlot = (slot) => {
        const newErrors = {};
        
        if (!slot.date) newErrors.date = t('scheduleInterview.errors.dateRequired') || 'Date requise';
        if (!slot.startTime) newErrors.startTime = t('scheduleInterview.errors.timeRequired') || 'Heure requise';
        if (!slot.endTime) newErrors.endTime = t('scheduleInterview.errors.timeRequired') || 'Heure requise';
        
        if (slot.startTime && slot.endTime && slot.startTime >= slot.endTime) {
            newErrors.endTime = t('scheduleInterview.errors.endTimeAfterStart') || 'L\'heure de fin doit être après l\'heure de début';
        }
        
        return newErrors;
    };

    const handleAddSlot = () => {
        const slotErrors = validateSlot(newSlot);
        
        if (Object.keys(slotErrors).length > 0) {
            setErrors(slotErrors);
            return;
        }

        onAddSlot({
            id: Date.now(),
            date: newSlot.date,
            startTime: newSlot.startTime,
            endTime: newSlot.endTime
        });

        setNewSlot({ date: '', startTime: '', endTime: '' });
        setErrors({});
    };

    const handleStartEdit = (slot) => {
        setEditingSlotId(slot.id);
        setEditSlot({ date: slot.date, startTime: slot.startTime, endTime: slot.endTime });
        setErrors({});
    };

    const handleSaveEdit = () => {
        const slotErrors = validateSlot(editSlot);
        
        if (Object.keys(slotErrors).length > 0) {
            setErrors(slotErrors);
            return;
        }

        onModifySlot(editingSlotId, editSlot);
        setEditingSlotId(null);
        setEditSlot({ date: '', startTime: '', endTime: '' });
        setErrors({});
    };


    return (
        <div className="space-y-6">
            {/* Interview Type and General Info */}
            <div className="bg-brand/5 border border-brand/20 rounded-lg p-4 space-y-4">
                <div>
                    <fieldset>
                        <legend className="block text-sm font-semibold text-main mb-3">
                            {t('scheduleInterview.form.type')} <span className="text-red-600">*</span>
                        </legend>
                        <div className="space-y-2">
                            <label className="flex items-center gap-3 cursor-pointer">
                                <input
                                    type="radio"
                                    name="type"
                                    value="ONLINE"
                                    checked={interviewType === 'ONLINE'}
                                    onChange={(e) => onFieldChange('interviewType', e.target.value)}
                                    className="w-4 h-4"
                                />
                                <span className="text-main">{t('scheduleInterview.form.typeOnline')}</span>
                            </label>
                            <label className="flex items-center gap-3 cursor-pointer">
                                <input
                                    type="radio"
                                    name="type"
                                    value="IN_PERSON"
                                    checked={interviewType === 'IN_PERSON'}
                                    onChange={(e) => onFieldChange('interviewType', e.target.value)}
                                    className="w-4 h-4"
                                />
                                <span className="text-main">{t('scheduleInterview.form.typeInPerson')}</span>
                            </label>
                        </div>
                    </fieldset>
                </div>

                {interviewType === 'ONLINE' && (
                    <div>
                        <label htmlFor="videoLink" className="block text-sm font-semibold text-main mb-2">
                            {t('scheduleInterview.form.videoLink')} <span className="text-red-600">*</span>
                        </label>
                        <input
                            id="videoLink"
                            type="url"
                            placeholder="https://zoom.us/j/123456789"
                            value={videoLink}
                            onChange={(e) => onFieldChange('videoLink', e.target.value)}
                            className={inputField + (submissionAttempted && !videoLink ? ' border-red-600 ring-red-200' : '')}
                        />
                        <p className="text-xs text-main/70 mt-1">
                            {t('scheduleInterview.form.videoLinkHint')}
                        </p>
                        {submissionAttempted && !videoLink && (
                            <p className={textError + ' text-sm mt-1'}>{t('scheduleInterview.errors.videoLinkRequired')}</p>
                        )}
                    </div>
                )}

                {interviewType === 'IN_PERSON' && (
                    <div>
                        <label htmlFor="address" className="block text-sm font-semibold text-main mb-2">
                            {t('scheduleInterview.form.address')} <span className="text-red-600">*</span>
                        </label>
                        <textarea
                            id="address"
                            placeholder="123 Rue Principale, Montréal, QC H1A 1A1"
                            value={address}
                            onChange={(e) => onFieldChange('address', e.target.value)}
                            rows="2"
                            className={inputField + (submissionAttempted && !address ? ' border-red-600 ring-red-200' : '')}
                        />
                        {submissionAttempted && !address && (
                            <p className={textError + ' text-sm mt-1'}>{t('scheduleInterview.errors.addressRequired')}</p>
                        )}
                    </div>
                )}

                <div>
                    <label htmlFor="notes" className="block text-sm font-semibold text-main mb-2">
                        {t('scheduleInterview.form.notes')}
                    </label>
                    <textarea
                        id="notes"
                        placeholder={t('scheduleInterview.form.notesPlaceholder')}
                        value={notes}
                        onChange={(e) => onFieldChange('notes', e.target.value)}
                        rows="2"
                        maxLength="500"
                        className={inputField}
                    />
                    <div className="flex justify-between items-center mt-1">
                        <p className="text-xs text-main/70">
                            {t('scheduleInterview.form.notesHelp')}
                        </p>
                        <span className="text-xs text-main/70">
                            {notes.length}/500
                        </span>
                    </div>
                </div>
            </div>

            {/* Add Slots Section */}
            <div className="border-2 border-dashed border-brand/30 rounded-lg p-4">
                <h3 className="font-semibold text-main mb-4">{t('scheduleInterview.slots.title')}</h3>
                
                <div className="space-y-3 mb-4 bg-white rounded-lg p-3">
                    <div>
                        <label htmlFor="slot-date" className="block text-sm font-semibold text-main mb-2">
                            {t('scheduleInterview.form.date')} <span className="text-red-600">*</span>
                        </label>
                        <input
                            id="slot-date"
                            type="date"
                            value={newSlot.date}
                            onChange={(e) => setNewSlot({ ...newSlot, date: e.target.value })}
                            min={today}
                            className={inputField + (errors.date ? ' border-red-600 ring-red-200' : '')}
                        />
                        {errors.date && <p className={textError + ' text-sm mt-1'}>{errors.date}</p>}
                    </div>

                    <div className="grid grid-cols-2 gap-2">
                        <div>
                            <label htmlFor="slot-start" className="block text-sm font-semibold text-main mb-2">
                                {t('scheduleInterview.form.startTime')} <span className="text-red-600">*</span>
                            </label>
                            <input
                                id="slot-start"
                                type="time"
                                value={newSlot.startTime}
                                onChange={(e) => setNewSlot({ ...newSlot, startTime: e.target.value })}
                                className={inputField + (errors.startTime ? ' border-red-600 ring-red-200' : '')}
                            />
                            {errors.startTime && <p className={textError + ' text-sm mt-1'}>{errors.startTime}</p>}
                        </div>

                        <div>
                            <label htmlFor="slot-end" className="block text-sm font-semibold text-main mb-2">
                                {t('scheduleInterview.form.endTime')} <span className="text-red-600">*</span>
                            </label>
                            <input
                                id="slot-end"
                                type="time"
                                value={newSlot.endTime}
                                onChange={(e) => setNewSlot({ ...newSlot, endTime: e.target.value })}
                                className={inputField + (errors.endTime ? ' border-red-600 ring-red-200' : '')}
                            />
                            {errors.endTime && <p className={textError + ' text-sm mt-1'}>{errors.endTime}</p>}
                        </div>
                    </div>

                    <button
                        type="button"
                        onClick={handleAddSlot}
                        className={`${btnPrimary} w-full`}
                    >
                        {t('scheduleInterview.slots.addSlot')}
                    </button>
                </div>

                {/* Slots List */}
                {slots.length > 0 && (
                    <div className="space-y-2">
                        {slots.map((slot) => (
                            <div key={slot.id} className="bg-white border border-brand/20 rounded-lg p-3">
                                {editingSlotId === slot.id ? (
                                    <div className="space-y-2">
                                        <input
                                            type="date"
                                            value={editSlot.date}
                                            onChange={(e) => setEditSlot({ ...editSlot, date: e.target.value })}
                                            min={today}
                                            className={inputField + (errors.date ? ' border-red-600' : '')}
                                        />
                                        <div className="grid grid-cols-2 gap-2">
                                            <input
                                                type="time"
                                                value={editSlot.startTime}
                                                onChange={(e) => setEditSlot({ ...editSlot, startTime: e.target.value })}
                                                className={inputField + (errors.startTime ? ' border-red-600' : '')}
                                            />
                                            <input
                                                type="time"
                                                value={editSlot.endTime}
                                                onChange={(e) => setEditSlot({ ...editSlot, endTime: e.target.value })}
                                                className={inputField + (errors.endTime ? ' border-red-600' : '')}
                                            />
                                        </div>
                                        <div className="flex gap-2">
                                            <button
                                                type="button"
                                                onClick={handleSaveEdit}
                                                className={`${btnAccept} flex-1`}
                                            >
                                                {t('scheduleInterview.slots.save')}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => setEditingSlotId(null)}
                                                className={`${btnOutline} flex-1`}
                                            >
                                                {t('scheduleInterview.slots.cancel')}
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="flex items-center justify-between">
                                        <div className="flex-1">
                                            <p className="font-medium text-main">
                                                {new Date(slot.date + 'T00:00:00').toLocaleDateString(
                                                i18n.language, DATE_FORMAT_OPTIONS
                                            )} • {slot.startTime} - {slot.endTime}
                                            </p>
                                        </div>
                                        <div className="flex gap-2">
                                            <button
                                                type="button"
                                                onClick={() => handleStartEdit(slot)}
                                                className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                                            >
                                                {t('scheduleInterview.slots.edit')}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => onRemoveSlot(slot.id)}
                                                className="text-red-600 hover:text-red-800 text-sm font-medium"
                                            >
                                                {t('scheduleInterview.slots.delete')}
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}

                {slots.length === 0 && (
                    <p className="text-center text-main/50 text-sm py-4">
                        {t('scheduleInterview.slots.noSlots')}
                    </p>
                )}

                {submissionAttempted && slots.length === 0 && (
                    <p className={textError + ' text-sm mt-2'}>{t('scheduleInterview.errors.fillAllFields')}</p>
                )}
            </div>
        </div>
    );
};

export default InterviewSlotsForm;
